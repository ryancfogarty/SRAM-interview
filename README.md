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

I've decided to go for a very naive and simple approach for handling access tokens; I will only persist the access token for the lifetime of the app instance. I won't persist the refresh token, and thus won't be refreshing my access token when the access token expires. This is a very bad UX, as the user will have to sign back in often (they kill the app instance, the OS kills the app instance, access token expires). However, it makes OAuth much easier for me, and it can be improved in the future if there is time. 

I've decided to not create a multi-module app because of the time overhead it takes to set up and maintain multiple build.gradle files. Instead I will have separate feature packages which will act as modules.

After implementing the logic for fetching segments within provided bounds, I've decided not to handle expired access tokens at this time. In the interest of creating a super LEAN solution, any API rejection of an expired access token will be treated the same as any other network error when request segments. This is bad for the UX, as the user will be able to retry the request even though it is guaranteed to fail, but they won't be aware of that. However, it does allow me to develop the happy-path quicker, and opens up an iterative improvement that can be made if time permits.