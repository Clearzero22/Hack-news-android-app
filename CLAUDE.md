# Claude 开发规范文档

## 🎯 项目概述

**项目名称**: Hacker News 应用 (Android Kotlin + Jetpack Compose)  
**项目类型**: Android 移动应用  
**开发框架**: Kotlin + Jetpack Compose + Material 3  
**架构模式**: MVVM + Repository Pattern  
**开发主题**: Hacker Terminal / Cyberpunk UI 风格  

---

## 📋 开发规范

### 🏗️ 项目结构规范

```
app/src/main/java/com/example/myapplication/
├── data/                    # 数据层
│   ├── api/                 # API 接口定义
│   ├── model/               # 数据模型
│   └── repository/          # 数据仓库层
├── ui/                      # UI 层
│   ├── components/           # 可复用 UI 组件
│   ├── screens/             # 页面组件
│   └── theme/               # 主题和样式
└── MainActivity.kt           # 主活动
```

### 📝 命名规范

#### **包命名**
- `data.api` - API 接口
- `data.model` - 数据模型
- `data.repository` - 数据仓库
- `ui.components` - UI 组件
- `ui.screens` - 页面组件
- `ui.theme` - 主题样式

#### **文件命名**
- **Activity**: `MainActivity.kt`
- **Fragment**: `[Name]Fragment.kt`
- **ViewModel**: `[Name]ViewModel.kt`
- **Repository**: `[Name]Repository.kt`
- **API**: `[Name]Api.kt`
- **Model**: `[Name].kt`
- **Component**: `[Name]Component.kt` 或 `[Name]Item.kt`
- **Screen**: `[Name]Screen.kt`

#### **变量和函数命名**
- **变量**: `camelCase`
- **函数**: `camelCase`
- **常量**: `UPPER_SNAKE_CASE`
- **类名**: `PascalCase`
- **私有成员**: 以 `_` 开头

#### **示例**
```kotlin
class HackerNewsRepository {
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()
    
    suspend fun loadTopStories(): Result<List<Story>> { }
    
    companion object {
        const val MAX_STORIES = 30
    }
}
```

### 🔧 代码规范

#### **架构模式**
1. **MVVM 分层**:
   - **Model**: 数据模型 (Story, User 等)
   - **View**: UI 组件 (Compose 函数)
   - **ViewModel**: 业务逻辑和状态管理
   - **Repository**: 数据访问层

2. **依赖注入**:
   - 使用构造函数注入
   - 避免全局单例 (除 Repository 外)
   - 保持依赖关系清晰

#### **异步编程**
```kotlin
// ✅ 正确的异步处理
suspend fun loadStories(): Result<List<Story>> {
    return try {
        val storyIds = api.getTopStories()
        val stories = loadStoriesParallel(storyIds)
        Result.success(stories)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// ✅ 并行请求优化
private suspend fun loadStoriesParallel(ids: List<Int>): List<Story> {
    return coroutineScope {
        ids.map { id ->
            async { getStory(id) }
        }.awaitAll().filterNotNull()
    }
}
```

#### **错误处理**
```kotlin
// ✅ 统一错误处理
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Throwable) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

// 使用示例
val result = repository.getTopStories()
result.fold(
    onSuccess = { stories -> /* 处理成功 */ },
    onFailure = { error -> /* 处理失败 */ }
)
```

### 🎨 UI 开发规范

#### **Jetpack Compose 最佳实践**
```kotlin
@Composable
fun HackerStoryItem(
    story: Story,
    onStoryClick: (Story) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onStoryClick(story) }
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // 内容
    }
}
```

#### **状态管理**
```kotlin
@Composable
fun NewsListScreen(
    viewModel: NewsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when {
        uiState.isLoading -> LoadingIndicator()
        uiState.error != null -> ErrorMessage(
            message = uiState.error,
            onRetry = { viewModel.refreshStories() }
        )
        else -> NewsList(stories = uiState.stories)
    }
}
```

### 🎨 Hacker 风格 UI 规范

#### **颜色规范**
```kotlin
object HackerColors {
    val Green = Color(0xFF00FF41)     // 经典黑客绿
    val DarkGreen = Color(0xFF00A826) // 深绿色
    val Black = Color(0xFF0A0A0A)     // 终端黑
    val Grey = Color(0xFF1A1A1A)      // 代码灰
    val White = Color(0xFFE0E0E0)     // 终端白
    val Cyan = Color(0xFF00D4FF)      // 赛博蓝
    val Red = Color(0xFFFF5555)       // 错误红
}
```

#### **字体规范**
- **主要字体**: `FontFamily.Monospace` (等宽字体)
- **标题**: 加粗，较大尺寸
- **正文**: 常规尺寸，保持可读性

#### **组件风格**
- **卡片**: 深色背景，绿色边框
- **按钮**: 方括号样式 `[BUTTON]`
- **状态**: 终端风格提示语
- **动画**: 简洁的闪烁和扫描线效果

---

## 🛠️ 开发流程规范

### 📋 开发流程

#### **1. 需求分析**
```markdown
## 功能需求: [Feature Name]
### 用户故事
- 作为 [User], 我想要 [Goal], 以便 [Reason]

### 验收标准
- [ ] [Acceptance Criteria 1]
- [ ] [Acceptance Criteria 2]

### 技术要求
- [ ] [Technical Requirement 1]
- [ ] [Technical Requirement 2]
```

#### **2. 设计阶段**
- 创建 UI 设计稿
- 确定 API 接口
- 设计数据模型
- 制定技术方案

#### **3. 开发阶段**
1. **创建数据模型**
2. **实现 API 接口**
3. **开发 Repository 层**
4. **创建 ViewModel**
5. **实现 UI 组件**
6. **集成测试**

#### **4. 测试阶段**
- 单元测试
- 集成测试
- UI 测试
- 性能测试

### 📝 任务管理

#### **TodoWrite 工具使用**
```kotlin
// ✅ 复杂任务必须使用 TodoWrite
TodoWrite(
    todos = [
        TodoItem(
            content = "创建 Hacker News API 接口",
            status = "pending", 
            activeForm = "创建 Hacker News API 接口"
        ),
        TodoItem(
            content = "实现并行请求优化",
            status = "in_progress",
            activeForm = "实现并行请求优化"
        )
    ]
)
```

#### **任务拆分原则**
- 单个任务不超过 2 小时工作量
- 任务描述要具体可执行
- 优先级明确：核心功能 > 优化 > 错误处理
- 依赖关系清晰

### 🔧 开发工具和库

#### **必需依赖**
```kotlin
// build.gradle.kts
dependencies {
    // Kotlin & Android
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    
    // 网络请求
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    
    // UI 组件
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // WebView (如需要)
    implementation("androidx.webkit:webkit:1.8.0")
}
```

#### **推荐库**
```kotlin
// UI 优化
implementation("io.coil-kt:coil-compose:2.5.0")     // 图片加载
implementation("com.google.accompanist:accompanist-swiperefresh:0.32.0")  // 下拉刷新

// 动画
implementation("com.airbnb.android:lottie-compose:6.1.0")
implementation("com.valentinilk.shimmer:compose-shimmer:1.2.0")
```

### 🔍 代码审查标准

#### **代码质量检查**
- [ ] **命名规范**: 遵循命名约定
- [ ] **架构清晰**: MVVM 分层合理
- [ ] **错误处理**: 完善的异常处理机制
- [ ] **性能优化**: 避免不必要的数据加载
- [ ] **代码复用**: 提取公共组件
- [ ] **注释充分**: 复杂逻辑要有注释

#### **性能检查**
- [ ] **异步操作**: 使用协程避免阻塞主线程
- [ ] **内存管理**: 避免内存泄漏
- [ ] **网络优化**: 实现缓存和并行请求
- [ ] **UI 性能**: 避免过度重组

#### **用户体验检查**
- [ ] **加载状态**: 提供清晰的加载指示
- [ ] **错误处理**: 友好的错误提示和重试机制
- [ ] **响应式设计**: 适配不同屏幕尺寸
- [ ] **交互反馈**: 适当的点击反馈和状态变化

---

## 🧪 测试规范

### 📋 测试类型

#### **单元测试**
```kotlin
// Repository 测试示例
@Test
fun `getTopStories returns success when API call succeeds`() = runTest {
    // Given
    val mockApi = mockk<HackerNewsApi>()
    whenever(mockApi.getTopStories()).thenReturn(listOf(1, 2, 3))
    whenever(mockApi.getStory(1)).thenReturn(testStory1)
    val repository = HackerNewsRepository(mockApi)
    
    // When
    val result = repository.getTopStories()
    
    // Then
    assertTrue(result.isSuccess)
    assertEquals(listOf(testStory1), result.getOrNull())
}
```

#### **集成测试**
```kotlin
@Test
fun `ViewModel loads stories and updates UI state`() = runTest {
    // Given
    val repository = mockk<HackerNewsRepository>()
    val viewModel = NewsViewModel(repository)
    
    // When
    viewModel.loadStories()
    
    // Then
    val uiState = viewModel.uiState.value
    assertTrue(uiState.stories.isNotEmpty())
    assertFalse(uiState.isLoading)
}
```

### 🎯 测试覆盖要求

#### **代码覆盖率目标**
- **数据层**: 90%+
- **业务逻辑**: 85%+
- **UI 组件**: 70%+
- **整体应用**: 75%+

#### **测试用例要求**
- 正常流程测试
- 异常情况测试
- 边界条件测试
- 性能测试 (加载时间)

---

## 📚 文档规范

### 📝 文档类型

#### **README.md**
- 项目介绍和功能特性
- 安装和运行指南
- 技术栈说明
- 开发环境配置

#### **API 文档**
- API 接口说明
- 数据格式定义
- 调用示例

#### **架构文档**
- 系统架构图
- 模块依赖关系
- 数据流向说明

### 📖 注释规范

#### **类注释**
```kotlin
/**
 * Hacker News 数据仓库类
 * 
 * 负责从 Hacker News API 获取数据，提供缓存和并行请求优化
 * 使用 MVVM 架构中的 Repository 模式
 * 
 * @author Claude
 * @since 1.0.0
 */
class HackerNewsRepository {
    // 实现
}
```

#### **函数注释**
```kotlin
/**
 * 获取热门新闻列表
 * 
 * @param limit 返回的最大新闻数量
 * @return Result<List<Story>> 包含新闻列表的结果
 * @throws IOException 网络请求失败时抛出
 */
suspend fun getTopStories(limit: Int = 30): Result<List<Story>>
```

#### **复杂逻辑注释**
```kotlin
// 使用并行请求优化性能
// 将原本的 30 次串行请求改为并行执行
// 预计提升 3-4 倍的加载速度
private suspend fun loadStoriesParallel(ids: List<Int>): List<Story> {
    return coroutineScope {
        ids.map { id ->
            async { getStory(id) }  // 创建并发协程
        }.awaitAll().filterNotNull()  // 等待所有请求完成
    }
}
```

---

## 🚀 部署和发布规范

### 🔧 构建配置

#### **构建变体**
- `debug`: 开发版本，包含调试信息
- `release`: 发布版本，优化混淆和大小

#### **混淆配置**
```kotlin
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
}
```

### 📱 版本管理

#### **版本号格式**
- **主版本**: 重大架构变更 (1.x.x)
- **次版本**: 新功能添加 (x.1.x)
- **修订版本**: Bug 修复 (x.x.1)

#### **发布流程**
1. 完成所有测试
2. 更新版本号
3. 构建发布 APK
4. 测试发布版本
5. 提交版本标签
6. 更新发布说明

---

## 🤝 团队协作规范

### 📋 Git 工作流

#### **分支策略**
- `main`: 主分支，稳定版本
- `develop`: 开发分支
- `feature/*`: 功能分支
- `bugfix/*`: 修复分支

#### **提交信息规范**
```markdown
[类型] 简洁描述

## 详细说明 (可选)

- 技术细节
- 影响范围
- 相关 Issue

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

#### **提交类型**
- `feat`: 新功能
- `fix`: Bug 修复
- `refactor`: 代码重构
- `test`: 测试相关
- `docs`: 文档更新
- `style`: 代码格式调整
- `perf`: 性能优化

### 🔍 代码审查

#### **审查清单**
- [ ] 代码符合项目规范
- [ ] 功能实现正确
- [ ] 性能表现良好
- [ ] 测试覆盖充分
- [ ] 文档更新完整
- [ ] 没有明显的 Bug

#### **审查反馈**
- 使用友好的语言提供建议
- 解释修改原因
- 提供解决方案
- 尊重团队成员的贡献

---

## 🚀 性能优化规范

### ⚡ 性能目标

#### **加载时间**
- **首次启动**: < 3 秒
- **页面切换**: < 0.5 秒
- **数据加载**: < 2 秒

#### **内存使用**
- **正常运行**: < 100MB
- **内存泄漏**: 无
- **缓存大小**: 合理控制

### 🔧 优化策略

#### **网络优化**
- 使用并行请求
- 实现智能缓存
- 减少不必要请求
- 优化图片加载

#### **UI 优化**
- 使用 Jetpack Compose 最佳实践
- 避免过度重组
- 优化列表滚动
- 合理使用动画

#### **内存优化**
- 及时释放资源
- 避免内存泄漏
- 优化缓存策略
- 监控内存使用

---

## 📊 监控和分析

### 📈 关键指标

#### **性能指标**
- 应用启动时间
- 页面加载时间
- 网络请求延迟
- 内存使用情况
- 电池消耗

#### **质量指标**
- 崩溃率
- ANR 率
- 网络错误率
- 用户留存率

---

## 🔮 常见问题解决

### 🐛 常见错误

#### **编译错误**
- 检查依赖版本兼容性
- 清理项目缓存
- 同步 Gradle 配置

#### **运行时错误**
- 检查网络权限
- 验证 API 调用
- 查看日志详情

#### **性能问题**
- 使用性能分析工具
- 检查内存泄漏
- 优化网络请求

### 🛠️ 调试技巧

#### **日志记录**
```kotlin
// 使用 Logcat 进行调试
Log.d("HackerNews", "Loading stories: $storyCount")
Log.e("HackerNews", "Network error", e)
```

#### **性能分析**
```kotlin
// 使用 Performance Monitor
android.util.Log.d("Performance", "Load time: $loadTime ms")
```

---

*此文档持续更新，请团队成员共同维护和完善。*

**最后更新**: 2024年11月1日  
**维护者**: Claude AI Assistant