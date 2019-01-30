# ifood-mobile-test
Create an app that given an Twitter username it will list user's tweets. When I tap one of the tweets the app will visualy indicate if it's a happy, neutral or sad tweet.

## Business rules
* Happy Tweet: We want a vibrant yellow color on screen with a 😃 emoji
* Neutral Tweet: We want a grey colour on screen with a 😐 emoji
* Sad Tweet: We want a blue color on screen with a 😔 emoji
* For the first release we will only support english language

## Implementation details

* Kotlin 😃
* MVVM clean architecture (Presentation, Domain and Data layers)
* Reactive programming with RxKotlin easily manage threads and layers communication
* Dependency injection with Koin to have clean code and testable class
* Room database to persist tweet analyzes
* SharedPreferences to cache user name searched
* Retrofit to fetch tweets on server
* Twitter API to list tweets with official views and reuse oauth implementation
* Google API to implement Natural language
* Mockito and Robolectric to unit and integration tests
* Databinding and LiveData to observable communication
