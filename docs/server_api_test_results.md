# 四大测试结果同步 - 服务器端开发需求

## 背景
客户端本地保存四大测试（MBTI、大五人格、星座、八字）的结果，按邮箱区分用户。点击"查看历史"时，需要将本地测试结果同步到服务器端保存。

## 流程
1. 用户点击"查看历史"
2. 客户端读取本地四大测试的JSON数据
3. 客户端将测试结果POST到服务器端
4. 服务器端按邮箱存库（用户表 + 测试结果表）

---

## 接口设计

### 1. 同步测试结果（批量上传）

**URL**: `POST https://jiehun.mynatapp.cc/user/sync-test-results`

**请求头**:
```
Content-Type: application/json
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "email": "user@example.com",
  "mbti": { ...MBTI测试结果JSON... },
  "bigfive": { ...大五人格测试结果JSON... },
  "constellation": { ...星座测试结果JSON... },
  "bazi": { ...八字测试结果JSON... }
}
```

**说明**:
- `email`: 用户邮箱（唯一标识）
- `mbti`: MBTI测试结果（对象，可为空/null表示无记录）
- `bigfive`: 大五人格测试结果
- `constellation`: 星座测试结果
- `bazi`: 八字测试结果
- 每个测试结果对象中包含 `timestamp` 字段用于判断最新记录

**响应**:
```json
{
  "success": true,
  "message": "同步成功",
  "syncedAt": "2024-01-15 10:30:00"
}
```

---

### 2. 获取用户测试结果

**URL**: `GET https://jiehun.mynatapp.cc/user/test-results?email=user@example.com`

**请求头**:
```
Authorization: Bearer {token}
```

**响应**:
```json
{
  "success": true,
  "data": {
    "mbti": { ... },
    "bigfive": { ... },
    "constellation": { ... },
    "bazi": { ... }
  }
}
```

---

## 数据库设计建议

### 用户表 (users)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| email | VARCHAR(255) | 邮箱（唯一） |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 测试结果表 (test_results)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 关联users表 |
| test_type | VARCHAR(50) | mbti/bigfive/constellation/bazi |
| result_json | TEXT | 测试结果JSON |
| test_timestamp | BIGINT | 测试时的时间戳 |
| created_at | DATETIME | 同步时间 |
| updated_at | DATETIME | 更新时间 |

**索引**: (user_id, test_type) 唯一索引，确保每种测试只存最新记录

---

## 客户端调用时机

在 `TestHistoryFragment` 的 `loadHistory()` 方法中，读取本地JSON后，调用同步接口：

```java
// 伪代码
private void loadHistory() {
    // 1. 读取本地测试结果
    List<MbtiResult> mbtiResults = TestResultStorage.loadMbtiResults(context);
    List<BigFiveResult> bigFiveResults = TestResultStorage.loadBigFiveResults(context);
    // ... 读取其他测试

    // 2. 构造同步数据
    JSONObject syncData = new JSONObject();
    syncData.put("email", userEmail);
    if (!mbtiResults.isEmpty()) {
        syncData.put("mbti", mbtiResults.get(mbtiResults.size()-1)); // 最新记录
    }
    // ... 其他测试

    // 3. 调用同步API
    // POST /user/sync-test-results
}
```

---

## 测试要点

| 测试项 | 预期 |
|--------|------|
| 新用户首次同步 | 成功写入数据库 |
| 重复同步（同一测试） | 更新而非重复插入 |
| 部分测试缺失 | 只同步有的测试，其他留空 |
| 无token | 返回401未授权 |
| 邮箱不存在 | 自动创建用户记录 |

---

## 注意事项
1. 服务器端需要对token进行验证，确保用户身份合法
2. 测试结果JSON中包含中文字段（如 `mbtiTypeCh`、`eScoreCh`），服务器直接存储即可
3. 优先存最新记录（比较timestamp），覆盖旧数据
