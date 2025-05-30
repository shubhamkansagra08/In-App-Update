# 📲 In-App Update Manager

A lightweight Android library to integrate **Google Play In-App Updates** with just a few lines of code.  
Supports both **Flexible** (user can defer) and **Immediate** (forced update) flows.

---

## ✨ Features

- ✅ Support for **Flexible** and **Immediate** updates  
- ✅ Auto-resume on app relaunch  
- ✅ Graceful handling of **failures** and **cancellations**  
- ✅ **Minimal setup** – just call `init()`  
- ✅ Written in Kotlin using the **Play Core Library**  

---

## 🚀 Installation

### Step 1️⃣: Add Dependency  

Add it to your `settings.gradle` with:

```gradle
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			maven { url=uri("https://jitpack.io") }
		}
	}
```

and:

Add the following dependency to your **app-level `build.gradle`** with:

```gradle
dependencies {
    implementation 'com.github.shubhamkansagra08:In-App-Update:v1.0.0'
}
```

For **Gradle (Kotlin DSL)**:

```kotlin
dependencies {
    implementation("com.github.shubhamkansagra08:In-App-Update:v1.0.0")
}
```
---
## 🛠️ Usage

### Initialize in `onCreate()`
Call `InAppUpdateManager.init()` in your **MainActivity** or any entry-point activity:

```kotlin
class MainActivity : AppCompatActivity(), InAppUpdateCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        InAppUpdateManager.init(this, true, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        InAppUpdateManager.destroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        InAppUpdateManager.handleResult(requestCode, resultCode)
    }

    override fun onUpdateSuccess() {
        startNextActivity()
    }

    override fun onUpdateCanceled() {
        Toast.makeText(this, "Update canceled", Toast.LENGTH_SHORT).show()
    }

    override fun onUpdateFailed() {
        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
    }
}
```

---

## 🔧 API Methods

| Method | Description |
|--------|------------|
| `init(activity: Activity, isForceUpdate: Boolean = false, callback: InAppUpdateCallback)` | Initializes the in-app update manager. If `isForceUpdate = true`, it forces an **Immediate** update. |
| `destroyUpdate()` | Cleans up update listeners to prevent memory leaks. |
| `handleResult(requestCode: Int, resultCode: Int)` | Handles the result of the update flow. |

---

## 📷 Screenshots

### 🔹 **Flexible Update (User can postpone)**
![Flexible Update](https://developer.android.com/static/images/app-bundle/flexible_flow.png)

### 🔹 **Immediate Update (Blocks UI until updated)**
![Immediate Update](https://developer.android.com/static/images/app-bundle/immediate_flow.png)

---

## 📄 License

```
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
---

## 💬 Support

Need help or want to contribute?

- 🐞 [Open an Issue](https://github.com/shubhamkansagra08/In-App-Update/issues)
- ⭐ Star the repo to support the project
- 🔁 Pull Requests are welcome!

---

> Created with ❤️ by [Shubham Kansagra](https://github.com/shubhamkansagra08)
