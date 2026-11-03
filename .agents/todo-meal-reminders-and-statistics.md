# Meal reminders, calorie totals, and statistics TODO

Baseline: `ae68b3f3` (`main`, `origin/main`); initial worktree had pre-existing changes `M FORK.md` and `?? AGENTS.md`. Do not overwrite either. The widget addition already routes through `FoodYouAppNavHost` to `FoodDiarySearch(date, mealId)`; preserve that route as the shared entry flow.

## Work
- [x] Add Android scheduled notifications using each configured `Meal.from` time, with a stable schedule per meal and rescheduling when meals change or device/time zone/boot changes. Follow Android notification permission and exact/inexact alarm constraints for the target SDK.
- [x] Make notification tap open the same `FoodDiarySearch` flow as the widget, with an explicit intended date and meal ID. Re-check the meal exists before routing; deleted targets go to meal setup. Delivery resolves the previous meal from current settings.
- [x] Show today's consumed kcal in the launcher widget using food-backed and manual diary entries; the app-level Flow collector refreshes it for database mutations and date changes.
- [x] Include today's consumed energy in each reminder.
- [x] Add a Material 3 statistics page and an app navigation entry point. Show 3, 7, and 14 day averages simultaneously, plus daily totals and current/previous calendar-week summaries. Exclude days with under 500 tracked kcal from averages and show contributing counts/no-data state.
- [x] Add focused tests for window boundaries, the <500 exclusion, reminder meal selection, and deterministic same-time ordering. `:app:testDebugUnitTest` passed 47 tests; `:app:assembleRelease -PforkVersionName=3.4.9-fork.local -PforkVersionCode=12300006` succeeded on 2026-11-03. The generated APK is unsigned and no emulator/device flow was run.

## Code map
- Widget provider: `app/src/androidMain/kotlin/com/maksimowiczm/foodyou/app/infrastructure/android/QuickAddWidgetProvider.kt`.
- Widget routing and current-meal selection: `app/src/commonMain/kotlin/com/maksimowiczm/foodyou/app/navigation/FoodYouAppNavHost.kt` (`mealEntryWidgetRequest`, `FoodDiarySearch`, `Meal.contains`).
- Meal configuration includes `from`/`to`: `fooddiary/domain/entity/Meal.kt`; persistence: `fooddiary/infrastructure/room/MealDao.kt` and `RoomMealRepository.kt`.
- Food search registers its ViewModel in `app/ui/food/diary/search/FoodDiarySearchModule.kt`; actual screen/view model should be reused for notification routing.
- Home screen uses Material 3 `Scaffold`/`TopAppBar` and home cards in `app/ui/home/master/HomeScreen.kt`; navigation routes and callbacks live in `FoodYouAppNavHost.kt`.
## Decisions supplied for implementation
- Treat 3/7/14 days as calendar windows ending today; exclude each day below 500 kcal from the average denominator, show eligible-day count, and provide an explicit no-data state.
- At each configured meal time, remind about the preceding configured meal. The first meal wraps to the last configured meal on the prior calendar date.
- The widget and reminder body show today's consumed kcal; statistics show per-date totals and averages.

## Implemented
- `ObserveDailyEnergyUseCase` observes a date's energy totals from the existing diary repositories, treating entries without energy data as zero and returning zero when no meals exist. Its Flow updates for diary additions, edits, and deletions.
- `FoodYouApplication` follows the current date and refreshes all launcher widget instances. Widget kcal is rounded to a whole number and localized in English and German.
- Meal reminders use `AlarmManager.setAndAllowWhileIdle` without exact-alarm access. Android can defer these inexact alarms during idle or power saving. Each meal alarm is independent; a delivery advances only that meal so same-time reminders are retained. A persisted fingerprint (meal IDs, start times, rank, and time zone) prevents unchanged process startup from canceling delayed alarms; boot/time/timezone events force a rebuild. No missed alarm replay is scheduled.
- Android 13+ notification permission is requested once on normal launcher start using the platform permission prompt. The meal-settings icon opens Android notification settings after that prompt has been handled. The meal reminder title and daily kcal body are localized for English and German.
- The widget stores the total with its local calendar date, ignores a stale prior-date value on provider updates, and requests the platform-minimum periodic update interval (30 minutes) to clear a previous day's cached total if the app process is stopped at midnight.
