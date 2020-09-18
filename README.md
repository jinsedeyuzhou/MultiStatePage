# MultiStatePage
[![](https://jitpack.io/v/Zhao-Yan-Yan/MultiStatePage.svg)](https://jitpack.io/#Zhao-Yan-Yan/MultiStatePage)

- 
## 使用场景
- 
## 下载Demo
- 
## MultiStatePage的功能及特点
- 
- 
- 
## 开始

### 添加依赖
Add the JitPack repository to your build file
```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency
```
dependencies {
    implementation 'com.github.Zhao-Yan-Yan:MultiStatePage:Tag'
}
```

### 1.初始化MultiStatePage

**Application**
```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MultiStatePage.registerDefault()
    }
}
```
**添加自定义State**
```kotlin
MultiStatePage
    .register(CustomState())
    .register(OtherState())
    ...
    .registerDefault()
```
**AndroidManifest.xml 引用**
```
<manifest>
    <application 
        android:name=".App">
    </application>
</manifest>
```

### 2.生成MultiStateContainer

#### 在View上使用
基础用法
```kotlin
val multiStateContainer = MultiStatePage.multiState(view)
```
`kotlin` 拓展方法
```kotlin
val multiStateContainer = view.multiState()
```
#### 在Activity 根View中使用
基础用法
```kotlin
class MultiStateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xxx)
       val multiStateContainer = MultiStatePage.multiStateActivity(this)
    }
}
```
`kotlin` 拓展方法
```kotlin
class MultiStateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xxx)
        val multiStateContainer = multiStateActivityRoot()
    }
}
```

#### 在Fragment根View中使用

```kotlin
class MultiStateFragment : BaseFragment<FragmentMultiStateBinding>() {
    private lateinit var multiStateContainer: MultiStateContainer
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_api, container, false)
        multiStateContainer = MultiStatePage.multiState(root)
        return multiStateContainer
    }
}
```

### 3.切换状态
调用  `MultiStateContainer.show<T>()` 方法

**默认内置3种状态**
```kotlin
val multiStateContainer = MultiStatePage.multiState(view)
//成功页 
multiStateContainer.show<SuccessState>()
//错误页
multiStateContainer.show<ErrorState>()
//空页面
multiStateContainer.show<EmptyState>()
//加载状态页
multiStateContainer.show<LoadingState>()
```

#### 动态设置state

```kotlin
multiStateContainer.show<ErrorState>{errorState->
    errorState.setErrorMsg("xxx出错了")
}
```

#### 设置重试回调

```kotlin
val multiStateContainer = MultiStatePage.multiState(view){
    Toast.makeText(context, "retry", Toast.LENGTH_SHORT).show()
}
```

### 自定义State
#### 1.继承`MultiState`
```kotlin
class LottieWaitingState : MultiState() {
    //State布局
    override fun layoutId(): Int = R.layout.multi_lottie_waiting

    override fun onMultiStateCreate(view: View) {
        //自定义逻辑处理
    }

    override fun enableReload(): Boolean = false
}
```

`enableReload()` 是否允许`retry`回调 `false`不允许

#### 2.使用前需register

```kotlin
class LottieExtActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MultiStatePage.register(LottieWaitingState())
        val multiStateContainer = multiStateActivityRoot()
        multiStateContainer.show<LottieWaitingState>()
    }
}
```
