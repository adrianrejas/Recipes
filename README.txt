This is the code for my implementation of the Baking App project for the Udacity Android nanodegree program.

The project has been implemented following the clean arquitecture reccomentadionts mixed with the arquitecture recommended by Google for new Android projects, based on the use of LiveDatas and data repositories.

Apart from the requirements of the exercise, I've added the following features in order to practice more:

- Implementation of the scroll up to refresh UI pattern with the SwipeRefreshLayout element.
- Save recipes at database, for being loaded from database once downloaded the first time from network.

The network interface has been implemented using retrofit library, with gson converter library for transforming JSON response to application objects. The classes representing these objects have been created with the help of http://www.jsonschema2pojo.org/ online application.

The entities used by the application have been created by my own and are translated from the Rest API entities.

For database persistence, Room component has been used, using their own entities (in order to follow clean arquitecture principles).

UI data filling relies mainly on Data Binding component.

The main external libraries used are the following:
- Dagger2: Dependency injection.
- Retrofit: Network info retrieval.
- Glide: Image loading management.
- Gson: JSON translation.

There are 5 test cases:
- One for testing the request of recipes from the network.
- Another one for testing the navigation in the recipes list once stored at database.
- Three for testing the navigation between the details of a recipe:
   - One for phones in landscape mode.
   - Another for phones in portrait mode.
   - A final one for tablets.

The application has been tested with the following devices, with no problems:
- Virtualized Google Nexus 5 5¨ with API 26.
- Real Xiaomi Mi5s 5.15¨ with API 26.
- Real Samsung Galaxy Tab A 10 2016 10¨ with API 27.
- Real Asus Zenpad 7¨ with API 21.
- Virtualized Google Pixel XL 5.7¨ with API 28.
- Virtualized Google Nexus 10 10¨with API 27.

My intention has been for main classes to have at least an initial comment if not autoexplanatory, in order to guide the review of the code. Additional comments have been added alon both java and XML code in order to explain decissions made.

Lint analysis has been passed, with no errors. A few warnings have been supressed. Apart from this, redundant and unused parameters warnings have been deliberately left without resolve them because as the application is still in development, I think it's better to remove unnecesary elements once it's sure they are unnecessary. There are some spelling warnings which have also been omitted on purpose.

Icons made by Freepik from www.flaticon.com

Font made by Indian Type Foundry