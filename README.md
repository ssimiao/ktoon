# Ktoon

Token-Oriented Object Notation (TOON) for Kotlin Multiplatform where you can use on your web, backend or mobile apps
> Think of TOON as a more efficient way to use LLM: use JSON programmatically, convert to TOON for LLM input.

## Download

### For release version

```kotlin
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.ssimiao:ktoon:<version>
}
```

## Usage

```kotlin
import io.magesoftware.Ktoon

data class User(val id: Int, val name: String, val tags: List<String>, val active: Boolean, val preferences: List<Any>)
data class Data(val user: User)

fun main() {
    val user = User(123, "Ada", listOf("reading", "gaming"), true, listOf<Any>())
    val data = Data(user)

    println(KToon.encode(data))
}

//// example output:
// user:
//   id: 123
//   name: Ada
//   tags[2]: reading,gaming
//   active: true
//   preferences[0]:
```

## Why TOON?

AI is becoming cheaper and more accessible, but larger context windows allow for larger data inputs as well. **LLM tokens still cost money** – and standard JSON is verbose and token-expensive:

```json
{
  "users": [
    { "id": 1, "name": "Alice", "role": "admin" },
    { "id": 2, "name": "Bob", "role": "user" }
  ]
}
```

TOON conveys the same information with **fewer tokens**:

```
users[2]{id,name,role}:
  1,Alice,admin
  2,Bob,user
```

## Key Features

- 💸 **Token-efficient:** typically 30–60% fewer tokens than JSON
- 🤿 **LLM-friendly guardrails:** explicit lengths and field lists help models validate output
- 🍱 **Minimal syntax:** removes redundant punctuation (braces, brackets, most quotes)
- 📐 **Indentation-based structure:** replaces braces with whitespace for better readability
- 🧺 **Tabular arrays:** declare keys once, then stream rows without repetition

## Acknowledgements

This is a port of https://github.com/johannschopplich/toon and https://github.com/felipestanzani/JToon to Kotlin
