> Android Screenshot library

[TOC]

# op-screenshot

`op-screenshot` is a library for monitor screen capture events of the system.

## Usege

1. build.gradle

```java
implementation 'com.openpix:op-screenshot:1.0.0'
```

2. permission

add it to `AndroidManifest.xml`

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

3. code

please see
[MainActivity.kt](./app/src/main/java/com/openpix/screenshot/test/MainActivity.kt)

# Release Version

## 1.0.0

- 2020-09-10
- first issue

## 1.0.1

- minSDK version support to 14
- add shot activity and shot view static function.
