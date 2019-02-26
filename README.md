# 通过地图中瓦片格子进行数据包下载demo

## 开发环境

- androidStudio 3.0.1
- android最低版本 19

## 功能

- 多线程下载功能
- 支持暂停 继续 删除 断点续传 启动后提示是否继续任务功能
- 显示数据包下载进度和下载大小
- 可进行基础功能包下载

## 依赖

```java
    api files('aars/MapboxGLAndroidSDK-release-20181212.aar')
    api ('com.jakewharton.timber:timber:4.7.0')
    api ('com.squareup.okhttp3:okhttp:3.8.1')
    api ('com.mapbox.mapboxsdk:mapbox-android-telemetry:3.1.3')
    api ('com.mapbox.mapboxsdk:mapbox-sdk-geojson:3.2.0')
    api ('com.mapbox.mapboxsdk:mapbox-android-gestures:0.2.0')
    debugCompile 'com.squareup.leakcanary:leakcanary-android:1.3'
    releaseCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.3'

    compile 'com.github.satyan:sugar:1.5'
    compile 'com.arialyy.aria:aria-core:3.5.1'
    annotationProcessor 'com.arialyy.aria:aria-compiler:3.5.1'
    compile 'com.squareup.retrofit2:converter-gson:2.3.0'
    compile 'io.reactivex:rxandroid:1.2.1'
    compile 'com.squareup.retrofit2:adapter-rxjava:2.3.0'

```