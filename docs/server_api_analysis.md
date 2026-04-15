# AI 分析接口 - 服务器端开发文档

## Base URL
```
https://jiehun.mynatapp.cc
```

## 认证方式
所有接口需要在请求头中携带 token：
```
X-Session-Token: <登录获取的token>
```

---

## 1. 触发分析（调用 DeepSeek 分析）

**URL**: `POST /user/test-analysis`

**请求头**:
```
Content-Type: application/json
X-Session-Token: <token>
```

**请求体** (可选):
```json
{
  "force": true
}
```
- `force`: 是否强制重新分析，默认 false（使用缓存）

**响应**:
```json
{
  "success": true,
  "message": "分析任务已提交"
}
```

---

## 2. 获取分析结果

**URL**: `GET /user/test-analysis`

**请求头**:
```
X-Session-Token: <token>
```

**响应**:
```json
{
  "success": true,
  "analyses": {
    "mbti": {
      "analysis": "根据您的MBTI类型INTJ...",
      "is_analyzed": true,
      "analyzed_at": "2024-01-15 10:30:00"
    },
    "bigfive": {
      "analysis": "您的大五人格分析结果...",
      "is_analyzed": true,
      "analyzed_at": "2024-01-15 10:30:00"
    },
    "constellation": {
      "analysis": "您的星座匹配分析...",
      "is_analyzed": true,
      "analyzed_at": "2024-01-15 10:30:00"
    },
    "bazi": {
      "analysis": "您的八字命理分析...",
      "is_analyzed": true,
      "analyzed_at": "2024-01-15 10:30:00"
    }
  }
}
```

**字段说明**:
- `analysis`: AI 分析的文本内容
- `is_analyzed`: 是否已完成分析
- `analyzed_at`: 分析完成时间

---

## 3. 获取分析状态

**URL**: `GET /user/test-analysis/status`

**请求头**:
```
X-Session-Token: <token>
```

**响应**:
```json
{
  "success": true,
  "status": {
    "mbti": { "is_analyzed": true, "analyzed_at": "..." },
    "bigfive": { "is_analyzed": false },
    "constellation": { "is_analyzed": true, "analyzed_at": "..." },
    "bazi": { "is_analyzed": false }
  }
}
```

---

## 4. 服务器端调用 DeepSeek 的提示词参考

服务器端在调用 DeepSeek 分析时，可以使用以下提示词结构：

### MBTI 分析提示词
```
请根据用户的MBTI类型 {mbtiType} ({title}) 进行婚恋匹配分析，
用户的各项得分为：外向{E}={eScore}、内向I={iScore}、感觉S={sScore}、直觉N={nScore}、
思考T={tScore}、情感F={fScore}、判断J={jScore}、知觉P={pScore}。
请从性格优势、恋爱模式、理想伴侣类型、相处建议等方面进行分析，输出200-300字的中文分析。
```

### 大五人格分析提示词
```
请根据用户的大五人格测试结果进行分析：
开放性={openness}、尽责性={conscientiousness}、外向性={extraversion}、
宜人性={agreeableness}、神经质={neuroticism}。
请从人格特点、婚恋适配度、相处建议等方面进行200-300字的中文分析。
```

### 星座分析提示词
```
请根据用户的星座 {selfZodiac} 进行婚恋匹配分析，
星座特质：{zodiacTrait}，匹配分数：{matchScore}。
请从星座性格、理想伴侣、相处模式等方面进行200-300字的中文分析。
```

### 八字分析提示词
```
请根据用户的八字信息进行分析：
出生年：{myBirthYear}年{myBirthMonth}月{myBirthDay}日{myBirthHour}时，
性别：{myGender}，所在地：{myLocation}。
请从命理特点、婚恋运势、理想伴侣特征等方面进行200-300字的中文分析。
```

---

## 测试要点

| 测试项 | 预期 |
|--------|------|
| 未登录请求 | 返回 401 Unauthorized |
| 无 token 请求 | 返回 401 Unauthorized |
| 成功获取分析 | 返回 200 + analyses JSON |
| 尚未分析的测试 | is_analyzed = false |
| 强制重新分析 | POST /user/test-analysis 带 force:true |

---

## 注意事项
1. 分析是异步的，可以先返回成功，再后台调用 DeepSeek
2. 分析结果需要存库，避免重复调用 DeepSeek
3. 需要从 test_results 表读取用户的测试数据
4. token 验证方式：X-Session-Token 请求头
