# Fix Admin Block Bypass and Login Security

The goal is to ensure that a blocked user cannot log in, cannot stay logged in after a restart, and is forced to log out if they are blocked while using the app (on the same session/device). We also improve registration to prevent duplicate emails caused by whitespace.

## User Review Required

> [!NOTE]
> The fix for real-time logout only works on the device where the blocking occurs (the admin's device if they are blocking themselves, or if the app was used by multiple accounts on one device). For a real multi-device scenario, a backend with push notifications or periodic polling would be required.

## Proposed Changes

### Data Layer

#### [UserRepository.kt](file:///C:/Users/asus/AndroidStudioProjects/CommunityEventManagement/app/src/main/java/com/example/communityeventmanagement/data/repository/UserRepository.kt)

- Convert `currentUser` to a `MutableState` using `by mutableStateOf` to make it observable by Compose.
- Import `getValue` and `setValue` from `androidx.compose.runtime`.

#### [AppDataRepository.kt](file:///C:/Users/asus/AndroidStudioProjects/CommunityEventManagement/app/src/main/java/com/example/communityeventmanagement/data/repository/AppDataRepository.kt)

- Update `initialize` to check `isBlocked` when restoring a session. If the user is blocked, clear the session instead of logging them in.
- Update `handleTrustedApplication` to ensure `currentUser` is updated if it was the target of the change (already does this, but good to verify).

---

### UI Layer

#### [AdminPanelScreen.kt](file:///C:/Users/asus/AndroidStudioProjects/CommunityEventManagement/app/src/main/java/com/example/communityeventmanagement/ui/screens/admin/AdminPanelScreen.kt)

- When toggling a user's block status, check if that user is the `AppState.currentUser`. If so, update `AppState.currentUser` with the new status. This ensures the app reacts immediately if the current user is blocked.

#### [Navigation.kt](file:///C:/Users/asus/AndroidStudioProjects/CommunityEventManagement/app/src/main/java/com/example/communityeventmanagement/navigation/Navigation.kt)

- Simplify `AppNavigation` to use `AppState.currentUser` directly now that it's observable.
- Add a `LaunchedEffect` to monitor `currentUser`. If it becomes blocked, show a notification/toast and log the user out.

#### [AuthScreen.kt](file:///C:/Users/asus/AndroidStudioProjects/CommunityEventManagement/app/src/main/java/com/example/communityeventmanagement/ui/screens/auth/AuthScreen.kt)

- Update the registration email check to use `trim()` and `ignoreCase = true` to prevent duplicate accounts for the same email with different casing or surrounding whitespace.

---

## Verification Plan

### Automated Tests
- I will verify the changes by running the app and performing the following steps manually (since there are no existing instrumented tests for these flows).

### Manual Verification
1. **Blocked Login**: Attempt to log in with a user that has been blocked by an admin. Verify that an error message "Akun ini telah diblokir oleh admin." is shown.
2. **Session Restore**: Block a user, restart the app. Verify that the user is NOT automatically logged in even if they had a previous session.
3. **Real-time Block (Same Device)**: As an admin, block another user. If that user was logged in (testing on same device), verify they are forced to log out or redirected.
4. **Duplicate Email Registration**: Try to register with ` user1@gmail.com ` when `user1@gmail.com` already exists. Verify it fails with "Email sudah terdaftar".
