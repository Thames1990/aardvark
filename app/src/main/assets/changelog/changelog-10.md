- Change JSON deserialization
  - Remove Gson
  - Add Moshi. Here's ten small reasons to prefer Moshi over Gson:
    - Upcoming Kotlin support
    - Qualifiers like `@HexColor int` permit multiple JSON representations for a single Java type.
    - The `@ToJson` and `@FromJson` make it easy to write and test custom JSON adapters.
    - `JsonAdapter.failOnUnknown()` lets you reject unexpected JSON data.
    - Predictable exceptions. Moshi throws `IOException` on IO problems and `JsonDataException` on type mismatches. Gson is all over the place.
    - `JsonReader.selectName()` avoids unnecessary UTF-8 decoding and string allocations in the common case.
    - You’ll ship a smaller APK. Gson is 227 KiB, Moshi+Okio together are 200 KiB.
    - Moshi won’t leak implementation details of platform types into your encoded JSON. This makes me afraid of Gson: `gson.toJson(SimpleTimeZone.getTimeZone("GMT"))`
    - Moshi doesn’t do weird HTML escaping by default. Look at Gson’s default encoding of "12 & 5 = 4" for an example.
    - No broken `Date` adapter installed by default.
- Change model to conform JSON deserialization changes
- Added `FirebaseAuth` for future login experiments
- Set `FirebaseAnalytics` screen names depending on current Activity/Fragment
- The `BottomNavigation` in `MainActivity` now uses a selector as `itemIconTint` and `itemTextColor` to be able to show the user that an item is disabled or enabled.