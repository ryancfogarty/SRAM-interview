# SRAM-interview

***Project details***

This project will allow the user to explore Strava segments near their location. If time permits, the app will also allow the user to explore segments for an arbitrary location by allowing them to set an exploration zone via Google maps.  

For the MVP, the app will allow the user to login and search for segments near their location. If time permits, I will iterate with the following improvements:
1. Display results on an embedded Google Maps display and link to the Google Maps app for directions to the segment starting point
2. Allow the user to set a zone of exploration to search for segments in an arbitrary location
3. Improve the UI, as it will be fairly basic in the MVP
4. Control the size of the exploration zone
5. Add additional fields (min climb cat & max climb cat) to the search


***Build steps***

I included my Strava API clientId and clientSecret to make building easier. If you want to replace these, you can update the build config fields in the app/build.gradle.kts file and update the required code (redirect_uri, intent-filter). Of course, it is not very secure to share these details, but I don't mind in this instance.


***Decision log***

****Oct 11****

I've decided to go for a very naive and simple approach for handling access tokens; I will only persist the access token for the lifetime of the app instance. I won't persist the refresh token, and thus won't be refreshing my access token when the access token expires. This is a very bad UX, as the user will have to sign back in often (they kill the app instance, the OS kills the app instance, access token expires). However, it makes OAuth much easier for me, and it can be improved in the future if there is time. 

****Oct 11****

I've decided to not create a multi-module app because of the time overhead it takes to set up and maintain multiple build.gradle files. Instead I will have separate feature packages which will act as modules.

****Oct 12****

After implementing the logic for fetching segments within provided bounds, I've decided not to handle expired access tokens at this time. In the interest of creating a super LEAN solution, any API rejection of an expired access token will be treated the same as any other network error when request segments. This is bad for the UX, as the user will be able to retry the request even though it is guaranteed to fail, but they won't be aware of that. However, it does allow me to develop the happy-path quicker, and opens up an iterative improvement that can be made if time permits.

****Oct 12****

In the interest of developing an ultra-LEAN solution due to the time constraint, I have foregone proper permission request handling. In current Android versions, if the user is prompted twice and denies permissions both times, future native prompts won't be shown. I haven't handled this case as I am focusing on building just the happy path at this time, with minimal edge case and error handling.


***Final notes***

I've run out of time after developing the MVP of the app. It is currently very basic, geared towards supporting a simple happy path with minimal edge-case and error handling in place to guide the user back to that happy path.

Without making excuses, I want to let it be known that due to my machines limitations, a good chunk of my 4 hours was spent watching Gradle build. While I tried to utilize this time to plan ahead, there is only so much time optimization possible. My personal machine is a early-2015 MacBook Air, which lacks the resources that the Gradle Monster requires.

Considering this, I'm happy with where I've gotten so far. The user can connect with Strava, and after granting location permissions they can see Strava segments near their location. The UI is very basic, but it serves the immediate purposes. I'm happy with my architecture, TDD approach, scalability of the app, and my plan for future iterations. Taking a LEAN approach allowed me to fulfill the requirements within the time limit and opened up the doors for various improvements.

Here is the list of improvements I would make, in order of priority (highest priority first):
1. Fix configuration change bug
    - Reproduction steps: Connect with Strava, fetch nearby segments, then rotate the app. If you now back out to the Login screen, it will show an error.
    - I ran out of time before I could properly diagnose this bug
2. Improve OAuth handling
    - Right now OAuth isn't fully implemented. Once the access token expires or if the basic in-memory cache is cleared (OS destroys application instance and rebuilds it), the app is designed to crash. This should be improved by using the refresh token to fetch a new access token. If the refresh token itself expires, the user should be prompted to re-connect with Strava.
3. Improve UX - The UX is very simple, and not particularly engaging. Here is a list of improvements I'd like to make:
    - I would love to incorporate an embedded Google Map where I could display the users current location and all of the nearby segments. There are multiple deliverables here: display map with users location, display start position of segments, display path of each segment, link out to GPS app to navigate to the start position
    - Display more details of the segments
    - Add alluring visuals
    - Develop a better theme (custom colors and text styles)
    - Implement custom animations
    - Handle different screen sizes (tablets, foldables, landscape orientation on standard smart phones) better. Right now the UI isn't particularly nice for these cases. 
4. Allow the user to explore segments in an arbitrary location
    - Allow the user to set the zone they want to explore in, and display segments inside that zone
5. Improve permission request handling
   - On current versions of the Android SDK, if the user denies the permission request twice, each subsequent tap on the "Explore segments near me" button won't do anything. To improve this, I would track how many times the user had been prompted, and if it exceeds 2, display a custom dialog which links to the System Settings for the app so the user can manually grant location permissions.

In summary, I'm happy with where I got. I believe taking a super LEAN approach allowed me to deliver most of the requirements with the blockers I encountered (mostly Gradle builds being so slow).