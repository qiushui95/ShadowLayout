# ShadowLayout  
## 阴影控件  [![](https://jitpack.io/v/qiushui95/ShadowLayout.svg)](https://jitpack.io/#qiushui95/ShadowLayout)

``` project build.gradle
allprojects {
    repositories {
        maven{url "https://jitpack.io"}
    }
}
```

```
dependencies {
    implementation("com.github.qiushui95:ShadowLayout:1.0.0")
}
```
## ShadowDrawable 
    
| 属性                    | 类型  | 说明           | 默认值    |
| ----------------------- | ----- | -------------- | --------- |
| shadowShowStart         | bool  | 是否显示左阴影 | true      |
| shadowShowTop           | bool  | 是否显示上阴影 | true      |
| shadowShowEnd           | bool  | 是否显示右阴影 | true      |
| shadowShowBottom        | bool  | 是否显示下阴影 | true      |
| shadowTopStartRadius    | int   | 左上方阴影圆角 | 20px      |
| shadowTopEndRadius      | int   | 右上方阴影圆角 | 20px      |
| shadowBottomEndRadius   | int   | 右下方阴影圆角 | 20px      |
| shadowBottomStartRadius | int   | 左下方阴影圆角 | 20px      |
| shadowColor             | color | 阴影颜色       | #30000000 |
| shadowSize              | int   | 阴影长度       | 20px      |

## layout属性  
1. ### ShadowConstraintLayout 
| 属性                      | 类型  | 对应关系                |
| ------------------------- | ----- | ----------------------- |
| shadowClShowStart         | bool  | shadowShowStart         |
| shadowClShowTop           | bool  | shadowShowTop           |
| shadowClShowEnd           | bool  | shadowShowEnd           |
| shadowClShowBottom        | bool  | shadowShowBottom        |
| shadowClRadius            | dimen |                         |
| shadowClTopStartRadius    | dimen | shadowTopStartRadius    |
| shadowClTopEndRadius      | dimen | shadowTopEndRadius      |
| shadowClBottomEndRadius   | dimen | shadowBottomEndRadius   |
| shadowClBottomStartRadius | dimen | shadowBottomStartRadius |
| shadowClColor             | color | shadowColor             |
| shadowClSize              | dimen | shadowSize              |