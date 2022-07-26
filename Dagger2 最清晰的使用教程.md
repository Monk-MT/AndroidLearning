# Dagger2 最清晰的使用教程
部分内容参考自：  
[\[Android\]使用 Dagger 2 依赖注入 - DI 介绍（翻译）](http://www.cnblogs.com/tiantianbyconan/p/5092083.html)  
[\[Android\]使用 Dagger 2 依赖注入 - API（翻译）](http://www.cnblogs.com/tiantianbyconan/p/5092525.html)

## 为什么网上这么多 dagger2 教程，我还写了这篇文章。

1.  找了很多 Dagger2 相关的博客，我看的脑浆炸裂……
2.  Dagger2 给我们带来了什么，大多数博文也没有说明
3.  手动写写，加深印象，骗骗粉丝 （手动滑稽）
4.  部分 Dagger2 的运作机制是我个人的臆测，比如 Dagger2 编译入口，不过应该八九不离十吧，测试了挺多次的，没有 @Component 的话是不会编译的 =。=

## 一、Dagger2 使用 Q&A

**Q1：dagger2 是什么，有什么用？**  
A1：dagger2 是一个基于 JSR-330 标准的依赖注入框架，在编译期间自动生成代码，负责依赖对象的创建。

**Q2：什么是 JSR-330**  
A2：JSR 即 Java Specification Requests，意思是 java 规范提要。  
而 JSR-330 则是 Java 依赖注入标准  
关于 JSR-330 可以阅读这篇文章[Java 依赖注入标准（JSR-330）简介](http://blog.csdn.net/dl88250/article/details/4838803)，随便看下就好了，不是重点。

**Q3：用 dagger2 提供依赖有什么好处**  
A:3：为了进一步解耦和方便测试，我们会使用依赖注入的方式构建对象。（可以看这篇文章[使用 Dagger2 前你必须了解的一些设计原则](https://www.jianshu.com/p/cc1427e385b5)）  
但是，在 Activity 中有可能出现这样的情况。

    public class LoginActivity extends AppCompatActivity {

        LoginActivityPresenter presenter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            OkHttpClient okHttpClient = new OkHttpClient();
            RestAdapter.Builder builder = new RestAdapter.Builder();
            builder.setClient(new OkClient(okHttpClient));
            RestAdapter restAdapter = builder.build();
            ApiService apiService = restAdapter.create(ApiService.class);
            UserManager userManager = UserManager.getInstance(apiService);
            
            UserDataStore userDataStore = UserDataStore.getInstance(
                    getSharedPreferences("prefs", MODE_PRIVATE)
            );

            
            presenter = new LoginActivityPresenter(this, userManager, userDataStore);
        }
    } 

其实我们需要的只是`LoginActivityPresenter`对象，但是因为使用依赖注入的原因，我们不得不在 LoginActivity 中初始化一大堆 Presenter 所需要的依赖。

现在不仅依赖于`LoginActivityPresenter`，还依赖`OkHttpClient ，UserManager ，RestAdapter`等。它们之中任何一个的构造改变了，或者 Presenter 构造改变了，我们都需要反复修改 LoginActivity 中的代码。

而 dagger 框架就解决了这种问题，使用 dagger2 框架后相同代码如下：

    public class LoginActivity extends AppCompatActivity {

        @Inject
        LoginActivityPresenter presenter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            
            getDependenciesGraph().inject(this);
        }
    } 

LoginActivity 瞬间清爽了。dagger2 框架可以让依赖注入独立于组件之外，不管 Presenter 的依赖怎么改，都不会对 LoginActivity 的代码照成任何影响，**这就是 dagger2 框架的好处了**

## 二、Dagger2 API

    public @interface Component {
        Class<?>[] modules() default {};
        Class<?>[] dependencies() default {};
    }

    public @interface Subcomponent {
        Class<?>[] modules() default {};
    }

    public @interface Module {
        Class<?>[] includes() default {};
    }

    public @interface Provides {
    }

    public @interface MapKey {
        boolean unwrapValue() default true;
    }

    public interface Lazy<T> {
        T get();
    } 

还有在 Dagger 2 中用到的定义在 [JSR-330](https://link.jianshu.com/?t=https://jcp.org/en/jsr/detail?id=330) （Java 中依赖注入的标准）中的其它元素：

    public @interface Inject {
    }

    public @interface Scope {
    }

    public @interface Qualifier {
    } 

## 三、@Inject 和 @Component

先来看一段没有使用 dagger 的依赖注入 Demo  
MainActivity 依赖 Pot， Pot 依赖 Rose

    public class Rose {
        public String whisper()  {
            return "热恋";
        }
    } 

    public class Pot {

        private Rose rose;

        @Inject
        public Pot(Rose rose) {
            this.rose = rose;
        }

        public String show() {
            return rose.whisper();
        }
    } 

    public class MainActivity extends AppCompatActivity {

        private Pot pot;

        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Rose rose = new Rose();
            pot = new Pot(rose);

            String show = pot.show();
            Toast.makeText(MainActivity.this, show, Toast.LENGTH_SHORT).show();
        }
    } 

使用 Dagger2 进行依赖注入如下：

    public class Rose {

        @Inject
        public Rose() {}

        public String whisper()  {
            return "热恋";
        }
    } 

    public class Pot {

        private Rose rose;

        @Inject
        public Pot(Rose rose) {
            this.rose = rose;
        }

        public String show() {
            return rose.whisper();
        }
    } 

    @Component
    public interface MainActivityComponent {
        void inject(MainActivity activity);
    } 

    public class MainActivity extends AppCompatActivity {

        @Inject
        Pot pot;

        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            
            
            DaggerMainActivityComponent.create().inject(this);
            String show = pot.show();
            Toast.makeText(MainActivity.this, show, Toast.LENGTH_SHORT).show();
        }
    } 

Dagger2 生成的代码保存在这里：

![](https://upload-images.jianshu.io/upload_images/2202079-cdf20511b8e40939.png)

Dagger2 apt.png

源码待会分析，现在先来了解下`@Inject`和`@Component`两个 API，想要使用 Dagger2 进行依赖注入，至少要使用到这两个注解。  
`@Inject`用于标记需要注入的依赖，或者标记用于提供依赖的方法。  
`@Component`则可以理解为注入器，在注入依赖的目标类`MainActivity`使用 Component 完成注入。

### @Inject

依赖注入中第一个并且是最重要的就是`@Inject`注解。JSR-330 标准中的一部分，标记那些应该被依赖注入框架提供的依赖。在 Dagger 2 中有 3 种不同的方式来提供依赖：

1.  **构造器注入，@Inject 标注在构造器上其实有两层意思。**  
    ①告诉 Dagger2 可以使用这个构造器构建对象。如`Rose`类  
    ②注入构造器所需要的参数的依赖。 如`Pot`类，构造上的 Rose 会被注入。  
    构造器注入的局限：如果有多个构造器，我们只能标注其中一个，无法标注多个。
2.  **属性注入**  
    如`MainActivity`类，标注在属性上。被标注的属性不能使用`private`修饰，否则无法注入。  
    属性注入也是 Dagger2 中使用最多的一个注入方式。
3.  **方法注入**


    public class MainActivity extends AppCompatActivity {

        private Pot pot;

        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            DaggerMainActivityComponent.create().inject(this);
            String show = pot.show();
            Toast.makeText(MainActivity.this, show, Toast.LENGTH_SHORT).show();
        }

        @Inject
        public void setPot(Pot pot) {
            this.pot = pot;
        }
    } 

标注在 public 方法上，Dagger2 会在构造器执行之后立即调用这个方法。  
方法注入和属性注入基本上没有区别， 那么什么时候应该使用方法注入呢？  
比如该依赖需要 this 对象的时候，使用方法注入可以提供安全的 this 对象，因为方法注入是在构造器之后执行的。  
比如 google mvp dagger2 中，给 View 设置 Presenter 的时候可以这样使用方法注入。

     @Inject
        void setupListeners() {
            mTasksView.setPresenter(this);
        } 

### @Component

`@Inject`注解只是 JSR-330 中定义的注解，在`javax.inject`包中。  
这个注解本身并没有作用，它需要依赖于注入框架才具有意义，用来标记需要被注入框架注入的方法，属性，构造。

而 Dagger2 则是用`Component`来完成依赖注入的，`@Component`可以说是 Dagger2 中最重要的一个注解。

    @Component
    public interface MainActivityComponent {
        void inject(MainActivity activity);
    } 

以上是定义一个 Component 的方式。使用接口定义，并且`@Component`注解。  
命名方式推荐为：`目标类名 + Component`，在编译后 Dagger2 就会为我们生成`DaggerXXXComponent`这个类，它是我们定义的`xxxComponent`的实现，在目标类中使用它就可以实现依赖注入了。

**Component 中一般使用两种方式定义方法。** 

1.  `void inject(目标类 obj);`Dagger2 会从目标类开始查找 @Inject 注解，自动生成依赖注入的代码，调用 inject 可完成依赖的注入。
2.  `Object getObj();` 如：`Pot getPot();`  
    Dagger2 会到 Pot 类中找被 @Inject 注解标注的构造器，自动生成提供 Pot 依赖的代码，这种方式一般为其他 Component 提供依赖。（一个 Component 可以依赖另一个 Component，后面会说）

**Component 和 Inject 的关系如下：** 

![](https://upload-images.jianshu.io/upload_images/2202079-51b78542dd3c8575.png)

Jsr330 和 Dagger2.png

Dagger2 框架以 Component 中定义的方法作为入口，到目标类中寻找 JSR-330 定义的 @Inject 标注，生成一系列提供依赖的 Factory 类和注入依赖的 Injector 类。  
而 Component 则是联系 Factory 和 Injector，最终完成依赖的注入。

**我们看下源码（请对应上面的 Dagger2 apt 图一起看）：** 

**Rose_Factory 和 Pot_Factory 分别对应 Rose 类和 Pot 类的构造器上的 @Inject 注解。**  
而 Factory 其实是个 Provider 对象

    public interface Provider<T> {

        
        T get();
    } 

    public interface Factory<T> extends Provider<T> {} 

为什么这里要使用枚举作为提供 Rose 对象的 Provide 我也不太清楚，反正能提供就对了 =。=

    public enum Rose_Factory implements Factory<Rose> {
      INSTANCE;

      @Override
      public Rose get() {
        return new Rose();
      }

      public static Factory<Rose> create() {
        return INSTANCE;
      }
    } 

Pot 对象依赖 Rose，所以直接将 RoseProvide 作为参数传入了。

    public final class Pot_Factory implements Factory<Pot> {
      private final Provider<Rose> roseProvider;

      public Pot_Factory(Provider<Rose> roseProvider) {
        assert roseProvider != null;
        this.roseProvider = roseProvider;
      }

      @Override
      public Pot get() {
        return new Pot(roseProvider.get());
      }

      public static Factory<Pot> create(Provider<Rose> roseProvider) {
        return new Pot_Factory(roseProvider);
      }
    } 

**MainActivity 上的 @Inject 属性或方法注解，则对应 MainActivity_MembersInjector 类**

    public interface MembersInjector<T> {

      
      void injectMembers(T instance);
    } 

    public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
      private final Provider<Pot> potProvider;

      public MainActivity_MembersInjector(Provider<Pot> potProvider) {
        assert potProvider != null;
        this.potProvider = potProvider;
      }

      public static MembersInjector<MainActivity> create(Provider<Pot> potProvider) {
        return new MainActivity_MembersInjector(potProvider);
      }

      @Override
      public void injectMembers(MainActivity instance) {
        if (instance == null) {
          throw new NullPointerException("Cannot inject members into a null reference");
        }
        instance.pot = potProvider.get();
      }

      public static void injectPot(MainActivity instance, Provider<Pot> potProvider) {
        instance.pot = potProvider.get();
      }
    } 

最后是 DaggerMainActivityComponent 类，对应 @Component 注解就不多说了。这是 Dagger2 解析 JSR-330 的入口。  
它联系 Factory 和 MainActivity 两个类完成注入。

    public final class DaggerMainActivityComponent implements MainActivityComponent {
      private Provider<Pot> potProvider;

      private MembersInjector<MainActivity> mainActivityMembersInjector;

      private DaggerMainActivityComponent(Builder builder) {
        assert builder != null;
        initialize(builder);
      }

      public static Builder builder() {
        return new Builder();
      }

      public static MainActivityComponent create() {
        return builder().build();
      }

      @SuppressWarnings("unchecked")
      private void initialize(final Builder builder) {

        this.potProvider = Pot_Factory.create(Rose_Factory.create());

        this.mainActivityMembersInjector = MainActivity_MembersInjector.create(potProvider);
      }

      @Override
      public void inject(MainActivity activity) {
        mainActivityMembersInjector.injectMembers(activity);
      }

      public static final class Builder {
        private Builder() {}

        public MainActivityComponent build() {
          return new DaggerMainActivityComponent(this);
        }
      }
    } 

只使用几个注解，Dagger2 就默默中为我们做了这么多事情，太感动了……  
看完这个，相信大家已经完全理解了 @Inject 和 @Component 两个注解的作用了，要区分的是，@Inject 是 JSR330 定义的，而 @Component 是 Dagger2 框架自己定义的。

## 四、@Module 和 @Provides

使用 @Inject 标记构造器提供依赖是有局限性的，比如说我们需要注入的对象是第三方库提供的，我们无法在第三方库的构造器上加上 @Inject 注解。  
或者，我们使用依赖倒置的时候，因为需要注入的对象是抽象的，@Inject 也无法使用，因为抽象的类并不能实例化，比如：

    public abstract class Flower {
        public abstract String whisper();
    } 

    public class Lily extends Flower {

        @Inject
        Lily() {}

        @Override
        public String whisper() {
            return "纯洁";
        }
    } 

    public class Rose extends Flower {

        @Inject
        public Rose() {}

        public String whisper()  {
            return "热恋";
        }
    } 

    public class Pot {

        private Flower flower;

        @Inject
        public Pot(Flower flower) {
            this.flower = flower;
        }

        public String show() {
            return flower.whisper();
        }
    } 

修改下 Demo，遵循依赖倒置规则。但是这时候 Dagger 就报错了，因为 Pot 对象需要 Flower，而 Flower 是抽象的，无法使用 @Inject 提供实例。

![](https://upload-images.jianshu.io/upload_images/2202079-798acb053f54eb7d.png)

抽象的依赖. png

这时候就需要用到 Module 了。

清除 Lily 和 Rose 的 @Inject

    public class Lily extends Flower {

        @Override
        public String whisper() {
            return "纯洁";
        }
    } 

    public class Rose extends Flower {

        public String whisper()  {
            return "热恋";
        }
    } 

@Module 标记在类上面，@Provodes 标记在方法上，表示可以通过这个方法获取依赖。

    @Module
    public class FlowerModule {
        @Provides
        Flower provideFlower() {
            return new Rose();
        }
    } 

在 @Component 中指定 Module

    @Component(modules = FlowerModule.class)
    public interface MainActivityComponent {
        void inject(MainActivity activity);
    } 

其他类不需要更改，这样就完成了。

那么 Module 是干嘛的，我们来看看生成的类。

![](https://upload-images.jianshu.io/upload_images/2202079-60ef03623d0d85c6.png)

Module.png

可以看到，被 @Module 注解的类生成的也是 Factory。

    public final class FlowerModule_FlowerFactory implements Factory<Flower> {
      private final FlowerModule module;

      public FlowerModule_FlowerFactory(FlowerModule module) {
        assert module != null;
        this.module = module;
      }

      @Override
      public Flower get() {
        return Preconditions.checkNotNull(
            module.provideFlower(), "Cannot return null from a non-@Nullable @Provides method");
      }

      public static Factory<Flower> create(FlowerModule module) {
        return new FlowerModule_FlowerFactory(module);
      }
    } 

@Module 需要和 @Provide 是需要一起使用的时候才具有作用的，并且 @Component 也需要指定了该 Module 的时候。

@Module 是告诉 Component，可以从这里获取依赖对象。Component 就会去找被 @Provide 标注的方法，相当于构造器的 @Inject，可以提供依赖。

还有一点要说的是，@Component 可以指定多个 @Module 的，如果需要提供多个依赖的话。  
并且 Component 也可以依赖其它 Component 存在。

## 五、@Qualifier 和 @Named

@Qualifier 是限定符，而 @Named 则是基于 String 的限定符。

当我有两个相同的依赖（都继承某一个父类或者都是先某一个接口）可以提供给高层时，那么程序就不知道我们到底要提供哪一个依赖，因为它找到了两个。  
这时候我们就可以通过限定符为两个依赖分别打上标记，指定提供某个依赖。

接着上一个 Demo，例如：Module 可以提供的依赖有两个。

    @Module
    public class FlowerModule {

        @Provides
        Flower provideRose() {
            return new Rose();
        }

        @Provides
        Flower provideLily() {
            return new Lily();
        }
    } 

![](https://upload-images.jianshu.io/upload_images/2202079-1c4a2b616d4e8781.png)

多个 Provider

这时候就可以用到限定符来指定依赖了，我这里用 @Named 来演示。

    @Module
    public class FlowerModule {

        @Provides
        @Named("Rose")
        Flower provideRose() {
            return new Rose();
        }

        @Provides
        @Named("Lily")
        Flower provideLily() {
            return new Lily();
        }
    } 

我们是通过 @Inject Pot 的构造器注入 Flower 依赖的，在这里可以用到限定符。

    public class Pot {

        private Flower flower;

        @Inject
        public Pot(@Named("Rose") Flower flower) {
            this.flower = flower;
        }

        public String show() {
            return flower.whisper();
        }
    } 

而 @Qualifier 的作用和 @Named 是完全一样的，不过更推荐使用 @Qualifier，因为 @Named 需要手写字符串，容易出错。

@Qualifier 不是直接注解在属性上的，而是用来自定义注解的。

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RoseFlower {} 

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface LilyFlower {} 

    @Module
    public class FlowerModule {

        @Provides
        @RoseFlower
        Flower provideRose() {
            return new Rose();
        }

        @Provides
        @LilyFlower
        Flower provideLily() {
            return new Lily();
        }
    } 

    public class Pot {

        private Flower flower;

        @Inject
        public Pot(@RoseFlower Flower flower) {
            this.flower = flower;
        }

        public String show() {
            return flower.whisper();
        }
    } 

* * *

我们也可以使用 Module 来管理 Pot 依赖，当然还是需要 @Qualifier 指定提供哪一个依赖

    @Module
    public class PotModule {

        @Provides
        Pot providePot(@RoseFlower Flower flower) {
            return new Pot(flower);
        }
    } 

然后 MainAcitivtyComponent 需要增加一个 Module

    @Component(modules = {FlowerModule.class, PotModule.class})
    public interface MainActivityComponent {
        void inject(MainActivity activity);
    } 

## 六、@Component 的 dependence 和 @SubComponent

[参考：SubComponent 和 Dependence 区别](https://link.jianshu.com/?t=http://stackoverflow.com/questions/29587130/dagger-2-subcomponents-vs-component-dependencies)

上面也说过，Component 可以依赖于其他 Component，可以使用 @Component 的 dependence，也可以使用 @SubComponent，这样就可以获取其他 Component 的依赖了。

如：我们也用 Component 来管理 FlowerModule 和 PotModule，并且使用 dependence 联系各个 Component。  
这次我就将代码贴完整点吧。

    public abstract class Flower {
        public abstract String whisper();
    } 

    public class Lily extends Flower {

        @Override
        public String whisper() {
            return "纯洁";
        }
    } 

    public class Rose extends Flower {

        public String whisper()  {
            return "热恋";
        }
    } 

    @Module
    public class FlowerModule {

        @Provides
        @RoseFlower
        Flower provideRose() {
            return new Rose();
        }

        @Provides
        @LilyFlower
        Flower provideLily() {
            return new Lily();
        }
    } 

Component 上也需要指定 @Qualifier

    @Component(modules = FlowerModule.class)
    public interface FlowerComponent {
        @RoseFlower
        Flower getRoseFlower();

        @LilyFlower
        Flower getLilyFlower();
    } 

    public class Pot {

        private Flower flower;

        public Pot(Flower flower) {
            this.flower = flower;
        }

        public String show() {
            return flower.whisper();
        }
    } 

PotModule 需要依赖 Flower，需要指定其中一个子类实现，这里使用 RoseFlower

    @Module
    public class PotModule {

        @Provides
        Pot providePot(@RoseFlower Flower flower) {
            return new Pot(flower);
        }
    } 

    @Component(modules = PotModule.class,dependencies = FlowerComponent.class)
    public interface PotComponent {
        Pot getPot();
    } 

    @Component(dependencies = PotComponent.class)
    public interface MainActivityComponent {
        void inject(MainActivity activity);
    } 

而在 MainActivity 则需要创建其依赖的 Component

    public class MainActivity extends AppCompatActivity {

        @Inject
        Pot pot;

        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            DaggerMainActivityComponent.builder()
                    .potComponent(DaggerPotComponent.builder()
                            .flowerComponent(DaggerFlowerComponent.create())
                            .build())
                    .build().inject(this);

            String show = pot.show();
            Toast.makeText(MainActivity.this, show, Toast.LENGTH_SHORT).show();
        }
    } 

这就是 Component 的 dependencies 的用法了，我们 Component 不需要重复的指定 Module，可以直接依赖其它 Component 获得。

分析下源码，看下 Component 的 dependencies 做了什么事情。

    public final class DaggerPotComponent implements PotComponent {
      private Provider<Flower> getRoseFlowerProvider;

      private Provider<Pot> providePotProvider;

      private DaggerPotComponent(Builder builder) {
        assert builder != null;
        initialize(builder);
      }

      public static Builder builder() {
        return new Builder();
      }

      @SuppressWarnings("unchecked")
      private void initialize(final Builder builder) {

        this.getRoseFlowerProvider =
            new Factory<Flower>() {
              private final FlowerComponent flowerComponent = builder.flowerComponent;

              @Override
              public Flower get() {
                return Preconditions.checkNotNull(
                    flowerComponent.getRoseFlower(),
                    "Cannot return null from a non-@Nullable component method");
              }
            };

        this.providePotProvider =
            PotModule_ProvidePotFactory.create(builder.potModule, getRoseFlowerProvider);
      }

      @Override
      public Pot getPot() {
        return providePotProvider.get();
      }

      public static final class Builder {
        private PotModule potModule;

        private FlowerComponent flowerComponent;

        private Builder() {}

        public PotComponent build() {
          if (potModule == null) {
            this.potModule = new PotModule();
          }
          if (flowerComponent == null) {
            throw new IllegalStateException(FlowerComponent.class.getCanonicalName() + " must be set");
          }
          return new DaggerPotComponent(this);
        }

        public Builder potModule(PotModule potModule) {
          this.potModule = Preconditions.checkNotNull(potModule);
          return this;
        }

        public Builder flowerComponent(FlowerComponent flowerComponent) {
          this.flowerComponent = Preconditions.checkNotNull(flowerComponent);
          return this;
        }
      }
    } 

PotComponent 依赖 FlowerComponent，其实就是将 FlowerComponent 的引用传递给 PotComponent，这样 PotComponent 就可以使用 FlowerComponent 中的方法了。  
注意看 getRoseFlowerProvider 这个 Provider，是从 `flowerComponent.getRoseFlower()`获取到的

* * *

如果使用 Subcomponent 的话则是这么写， 其他类不需要改变，只修改 Component 即可

    @Component(modules = FlowerModule.class)
    public interface FlowerComponent {
        
        PotComponent plus(PotModule potModule);
    } 

    @Subcomponent(modules = PotModule.class)
    public interface PotComponent {
        MainActivityComponent plus();
    } 

    @Subcomponent
    public interface MainActivityComponent {
        void inject(MainActivity activity);
    } 

    public class MainActivity extends AppCompatActivity {

        @Inject
        Pot pot;

        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            DaggerFlowerComponent.create()
                    .plus(new PotModule())  
                    .plus()                 
                    .inject(this);

            String show = pot.show();
            Toast.makeText(MainActivity.this, show, Toast.LENGTH_SHORT).show();
        }
    } 

FlowerComponent 管理了 PotComponent 和 MainActivityComponent，看起来不符合常理。

先来说说 Component 中的方法的第三种定义方式（上面说了两种）。

    @Component
    class AComponpent {
        XxxComponent plus(Module... modules)
    } 

    @Subcomponent(modules = xxxxx)
    class XxxComponent {
        
    } 

xxxComponent 是该 AComponpent 的依赖，被 @Subcomponent 标注。  
而 modules 参数则是 xxxComponent 指定的 Module。  
在重新编译后，Dagger2 生成的代码中，Subcomponent 标记的类是 Componpent 的内部类。  
像上面的 Demo，MainActivityComponent 是 PotComponent 的内部类，而 PotComponent 又是 FlowerComponent 的内部类。

* * *

但是用 Subcomponent 怎么看怎么别扭，各个 Component 之间联系太紧密，不太适合我们 Demo 的使用场景。  
**那什么时候该用 @Subcomponent 呢？**  
Subcomponent 是作为 Component 的拓展的时候。  
像我写的 Demo 中，Pot 和 Flower 还有 MainActivity 只是单纯的依赖关系。就算有，也只能是 Flower 作为 Pot 的 Subcomponent，而不是 Demo 中所示，因为我需要给大家展示 Dagger 的 API，强行使用。

**比较适合使用 Subcomponent 的几个场景：**  
很多工具类都需要使用到 Application 的 Context 对象，此时就可以用一个 Component 负责提供，我们可以命名为 AppComponent。  
需要用到的 context 对象的 SharePreferenceComponent，ToastComponent 就可以它作为 Subcomponent 存在了。

而且在 AppComponent 中，我们可以很清晰的看到有哪些子 Component，因为在里面我们定义了很多`XxxComponent plus(Module... modules)`

每个 ActivityComponent 也是可以作为 AppComponent 的 Subcomponent，这样可以更方便的进行依赖注入，减少重复代码。

**Component dependencies 和 Subcomponent 区别**

1.  Component dependencies 能单独使用，而 Subcomponent 必须由 Component 调用方法获取。
2.  Component dependencies 可以很清楚的得知他依赖哪个 Component， 而 Subcomponent 不知道它自己的谁的孩子…… 真可怜
3.  使用上的区别，Subcomponent 就像这样`DaggerAppComponent.plus(new SharePreferenceModule());`  
    使用 Dependence 可能是这样`DaggerAppComponent.sharePreferenceComponent(SharePreferenceComponent.create())`

**Component dependencies 和 Subcomponent 使用上的总结**

Component Dependencies：

1.  你想保留独立的想个组件（Flower 可以单独使用注入，Pot 也可以）
2.  要明确的显示该组件所使用的其他依赖

Subcomponent：

1.  两个组件之间的关系紧密
2.  你只关心 Component，而 Subcomponent 只是作为 Component 的拓展，可以通过 Component.xxx 调用。

[Dagger 2 subcomponents vs component dependencies](https://link.jianshu.com/?t=http://stackoverflow.com/questions/29587130/dagger-2-subcomponents-vs-component-dependencies)

## 七、@Scope 和 @Singleton

@Scope 是用来管理依赖的生命周期的。它和 @Qualifier 一样是用来自定义注解的，而 @Singleton 则是 @Scope 的默认实现。

     @Scope
    @Documented
    @Retention(RUNTIME)
    public @interface Singleton {} 

Component 会帮我们注入被 @Inject 标记的依赖，并且可以注入多个。  
但是每次注入都是重新 new 了一个依赖。如

    public class MainActivity extends AppCompatActivity {

        private static final String TAG = "MainActivity";

        @Inject
        Pot pot;

        @Inject
        Pot pot2;

        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            DaggerMainActivityComponent.builder()
                    .potComponent(DaggerPotComponent.builder()
                                    .flowerComponent(DaggerFlowerComponent.create()).build())
                    .build().inject(this);

            Log.d(TAG, "pot = " + pot.hashCode() +", pot2 = " + pot2.hashCode());

            String show = pot.show();
            Toast.makeText(MainActivity.this, show, Toast.LENGTH_SHORT).show();
        }
    } 

打印的地址值不一样，是两个对象。  
`D/MainActivity: pot = com.aitsuki.architecture.pot.Pot@240f3ff5, pot2 = com.aitsuki.architecture.pot.Pot@2c79118a`

假设我们需要 Pot 对象的生命周期和 app 相同，也就是单例，我们需要怎么做？这时候就可以用到 @Scope 注解了。

我们来使用默认的 @Scope 实现——@Singleton  
需要在 @Provide 和 @Component 中同时使用才起作用，为什么呢，待会会说明。

    @Module
    public class PotModule {

        @Provides
        @Singleton
        Pot providePot(@RoseFlower Flower flower) {
            return new Pot(flower);
        }
    } 

    @Singleton
    @Component(modules = PotModule.class, dependencies = FlowerComponent.class)
    public interface PotComponent {
        Pot getPot();
    } 

然后我们再运行下项目，报错了

![](https://upload-images.jianshu.io/upload_images/2202079-b60d581820901e83.png)

@Scope 报错

那是因为我们的 MainActivityComponent 依赖 PotComponent，而 dagger2 规定子 Component 也必须标注 @Scope。  
但是我们不能给 MainActivityComponent 也标注 @Singleton，并且 dagger2 也不允许。因为单例依赖单例是不符合设计原则的，我们需要自定义一个 @Scope 注解。

定义 Scope 是名字要起得有意义，能一眼就让你看出这个 Scope 所规定的生命周期。  
比如 ActivityScope 或者 PerActivity，生命周期和 Activity 相同。

    @Scope
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ActivityScope {} 

    @ActivityScope
    @Component(dependencies = PotComponent.class)
    public interface MainActivityComponent {
        void inject(MainActivity activity);
    } 

`D/MainActivity: pot = com.aitsuki.architecture.pot.Pot@240f3ff5, pot2 = com.aitsuki.architecture.pot.Pot@240f3ff5`  
这时候我们看到两个 pot 对象的地址值是一样的，@Scope 注解起作用了。

那么我再新建一个 Activity，再次注入 pot 打印地址值。

    public class SecondActivity extends AppCompatActivity {

        private static final String TAG = "SecondActivity";

        @Inject
        Pot pot3;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            DaggerSecondActivityComponent.builder()
                    .potComponent(DaggerPotComponent.builder().flowerComponent(DaggerFlowerComponent.create()).build())
                    .build().inject(this);

            Log.d(TAG, "pot3 = " + pot3);
        }
    } 

    @ActivityScope
    @Component(dependencies = PotComponent.class)
    public interface SecondActivityComponent {
        void inject(SecondActivity activity);
    } 

在 MainActivity 初始化时直接跳转到 SecondActivity

    public class MainActivity extends AppCompatActivity {

        private static final String TAG = "MainActivity";

        @Inject
        Pot pot;

        @Inject
        Pot pot2;

        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            DaggerMainActivityComponent.builder()
                    .potComponent(DaggerPotComponent.builder()
                                    .flowerComponent(DaggerFlowerComponent.create()).build())
                    .build().inject(this);

            Log.d(TAG, "pot = " + pot +", pot2 = " + pot2);

            String show = pot.show();
            Toast.makeText(MainActivity.this, show, Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, SecondActivity.class));
        }
    } 

`D/MainActivity: pot = com.aitsuki.architecture.pot.Pot@240f3ff5, pot2 = com.aitsuki.architecture.pot.Pot@240f3ff5`  
`D/SecondActivity: pot3 = com.aitsuki.architecture.pot.Pot@1b7661c7`

可以看到，在 SecondActivity 中，Pot 对象地址和 MainActivity 中的不一样了。  
为什么呢？不是叫 @Singleton 么，为什么使用了它 Pot 还不是单例的，Dagger2 你逗我！

* * *

那么现在我可以说说 @Scope 的作用了，它的作用只是保证依赖在 @Component 中是唯一的，可以理解为 “局部单例”。  
**@Scope 是需要成对存在的，在 Module 的 Provide 方法中使用了 @Scope，那么对应的 Component 中也必须使用 @Scope 注解，当两边的 @Scope 名字一样时（比如同为 @Singleton）, 那么该 Provide 方法提供的依赖将会在 Component 中保持 “局部单例”。  
而在 Component 中标注 @Scope，provide 方法没有标注，那么这个 Scope 就不会起作用，而 Component 上的 Scope 的作用也只是为了能顺利通过编译，就像我刚刚定义的 ActivityScope 一样。** 

@Singleton 也是一个自定义 @Scope，它的作用就像上面说的一样。但由于它是 Dagger2 中默认定义的，所以它比我们自定义 Scope 对了一个功能，就是编译检测，防止我们不规范的使用 Scope 注解，仅此而已。

在上面的 Demo 中，Pot 对象在 PotComponent 中是 “局部单例” 的。  
而到了 SecondActivity，因为是重新 Build 了一个 PotComponent，所以 Pot 对象的地址值也就改变了。

**那么，我们如何使用 Dagger2 实现单例呢？**  
很简单，做到以下两点即可。

1.  依赖在 Component 中是单例的（供该依赖的 provide 方法和对应的 Component 类使用同一个 Scope 注解。）
2.  对应的 Component 在 App 中只初始化一次，每次注入依赖都使用这个 Component 对象。（在 Application 中创建该 Component）

如：

    public class App extends Application {

        private PotComponent potComponent;

        @Override
        public void onCreate() {
            super.onCreate();
            potComponent = DaggerPotComponent.builder()
                    .flowerComponent(DaggerFlowerComponent.create())
                    .build();
        }
        
        public PotComponent getPotComponent() {
            return potComponent;
        }
    } 

然后修改 MainActivity 和 SecondActivity 的 Dagger 代码如下

    public class MainActivity extends AppCompatActivity {

        private static final String TAG = "MainActivity";

        @Inject
        Pot pot;

        @Inject
        Pot pot2;

        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            DaggerMainActivityComponent.builder()
                    .potComponent(((App) getApplication()).getPotComponent())
                    .build().inject(this);

            Log.d(TAG, "pot = " + pot +", pot2 = " + pot2);

            String show = pot.show();
            Toast.makeText(MainActivity.this, show, Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, SecondActivity.class));
        }
    } 

    public class SecondActivity extends AppCompatActivity {

        private static final String TAG = "SecondActivity";

        @Inject
        Pot pot3;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            DaggerSecondActivityComponent.builder()
                    .potComponent(((App) getApplication()).getPotComponent())
                    .build().inject(this);

            Log.d(TAG, "pot3 = " + pot3);
        }
    } 

运行后的 log 输出  
`D/MainActivity: pot = com.aitsuki.architecture.pot.Pot@240f3ff5, pot2 = com.aitsuki.architecture.pot.Pot@240f3ff5`  
`D/SecondActivity: pot3 = com.aitsuki.architecture.pot.Pot@240f3ff5`  
现在 Pot 的生命周期就和 app 相同了。

你也可以试试自定义一个 @ApplicationScope，替换掉 @Singleton，结果是一样的，这里就不演示了。

稍微总结下 @Scope 注解：  
**Scope 是用来给开发者管理依赖的生命周期的，它可以让某个依赖在 Component 中保持 “局部单例”（唯一），如果将 Component 保存在 Application 中复用，则可以让该依赖在 app 中保持单例。 我们可以通过自定义不同的 Scope 注解来标记这个依赖的生命周期，所以命名是需要慎重考虑的。**  
@Singleton 告诉我们这个依赖时单例的  
@ActivityScope 告诉我们这个依赖的生命周期和 Activity 相同  
@FragmentScope 告诉我们这个依赖的生命周期和 Fragment 相同  
@xxxxScope ……

## 八、MapKey 和 Lazy

### @MapKey

这个注解用在定义一些依赖集合（目前为止，Maps 和 Sets）。让例子代码自己来解释吧：  
定义：

    @MapKey(unwrapValue = true)
    @interface TestKey {
        String value();
    } 

提供依赖：

    @Provides(type = Type.MAP)
    @TestKey("foo")
    String provideFooKey() {
        return "foo value";
    }

    @Provides(type = Type.MAP)
    @TestKey("bar")
    String provideBarKey() {
        return "bar value";
    } 

使用：

    @Inject
    Map<String, String> map;

    map.toString() 

@MapKey 注解目前只提供两种类型 - String 和 Enum。

### Lazy

Dagger2 还支持 Lazy 模式，通过 Lazy 模拟提供的实例，在 @Inject 的时候并不初始化，而是等到你要使用的时候，主动调用其. get 方法来获取实例。  
比如：

    public class MainActivity extends AppCompatActivity {

        private static final String TAG = "MainActivity";

        @Inject
        Lazy<Pot> potLazy;

        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            DaggerMainActivityComponent.builder()
                    .potComponent(((App) getApplication()).getPotComponent())
                    .build().inject(this);

            Pot pot = potLazy.get();
            String show = pot.show();
            Toast.makeText(MainActivity.this, show, Toast.LENGTH_SHORT).show();
        }
    } 

## 九、项目实战

略……

233333333，直接去看 Google 的 MVP 模式吧，上面有例子，也可以去看看其他博客。  
我也不知道写不写哈，有点小忙，就算写也可能是国庆过后了。

## 十、完结

看完这篇博文之后，感觉如何？博主表示写的算是很详细，很清晰易懂了。不懂的可以跟着思路敲一下哦，不动手，永远不会知道 Dagger2 其实并没有想象的那么难用…… 
 [https://www.jianshu.com/p/24af4c102f62](https://www.jianshu.com/p/24af4c102f62)
