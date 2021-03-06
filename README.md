# Sakura Progress

A good look progress implementation in Kotlin.

![demo](doc/demo.gif)

## Screenshot

![screenshot](doc/screenshot.png)

## Usage

In xml:
```xml
<tt.kao.sakuraprogress.ui.SakuraProgress
    android:id="@+id/sakuraProgress"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

In code:
```kotlin
val sakuraProgress = findViewById(R.id.sakuraProgress)
sakuraProgress.progress = progress // set current progress value
sakuraProgress.petalNum = petalNumber // set number of petals in one falling
sakuraProgress.petalFloatTime = floatTime // set time of petal falling 
sakuraProgress.petalRotateTime = rotateTime // set time of petal rotation
```

## Reference

[GALeafLoading](https://github.com/Ajian-studio/GALeafLoading)

Thanks this guy to give me an idea to complete this.

## License

Copyright 2017 Silvius Kao

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
