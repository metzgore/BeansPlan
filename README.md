<img src="/misc/ic_launcher-web.png" width="20%" align="left" hspace="1" vspace="1">

# Beans Plan

**Beans Plan** is an Android app which downloads and shows the daily and weekly schedules of the online streaming station [Rocketbeans TV](https://www.rocketbeans.tv/).
The schedule data is used from <http://api.rbtv.rodney.io>.

</br>

## Screenshots
Tagesplan

<img src="/screenshots/screenshot_daily_schedule_phone.png" width="40%">

Wochenplan

<img src="/screenshots/screenshot_weekly_schedule_phone.png" width="40%">

## Used patterns and libraries
The app utilizes MVVM pattern, Android LiveData, ViewModel, Data Binding, and various libraries, including:
* [Retrofit](https://square.github.io/retrofit/)
* [Dagger](https://google.github.io/dagger/)
* [Gson](https://github.com/google/gson)
* [commons-lang](https://github.com/apache/commons-lang)

## Current Features
* Download and show daily and weekly schedules
* Cache schedules for offline usage
* Go to current day in weekly schedule
* Highlight current running show

## Planned features
* Customization options (e.g. hide past shows)
* Android Wear app
* Widget
* App Short Cuts
* Filter by program type
* Detail view for shows
* Possibly dark theme

## License

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
