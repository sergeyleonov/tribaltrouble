Tribal Trouble Y18 Edition
==============
Maven-based release of Tribal Trouble game originally released by Oddlabs in 2004 and later published under GPL2 license.

The project was cloned from [sunenielsen](https://github.com/sunenielsen/tribaltrouble.git) public repo and reworked. You can find the original readme below. Note that the build commands in the old readme will not work for this project. For the actual instructions refer to the main part of this readme.

Prerequisites
--------
* [Java SE Development Kit 6u45](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase6-419409.html). Note that newer releases fail to launch the game on 64-bit Windows because only 32-bit dll libraries are included in the project.  

* [Maven 3.2.5](https://archive.apache.org/dist/maven/maven-3/3.2.5/). Newer releases will not work under JDK 1.6.

Startup
--------
First, you need to build the resources bundle on which the main game executable depends. Then you can launch the game without the resources being rebuild every time.

Note that all the commands are run against the project root (where this readme file is located).

#### Build the game resources
```
mvn clean install -P build-geometry,convert-textures
```

#### Launch the game
```
cd tt
mvn clean install -P run-game
```

#### Build and run the game
```
mvn clean install -P build-geometry,convert-textures,run-game
```

#### Build the bundle
```
mvn clean install -P build-geometry,convert-textures,bundle
```

#### Launch the game from the bundle
* Unpack zip package found in the *bundle/target* dir
* Run ```java -cp "common-1.0-SNAPSHOT.jar;resources-1.0-SNAPSHOT.jar;tt-1.0-SNAPSHOT.jar;lib/lwjgl.jar;lib/lwjgl_util.jar;lib/jorbis.jar" -Djava.library.path=lib/native com.oddlabs.tt.Main```

#### Launch the servlets
```
cd servlets
clean install -P run-servlets
```

#### Launch the server
```
cd server
clean install -P run-server
```
The server starts on *localhost:33214*

#### Register the game
* Update your *settings* file by adding the following line:
    ```
    registration_address=localhost:8050
    ```
* Launch the *servlets* module
* Launch the game and choose *Register* from the menu
* Use 'G35S-AAAA-AAAA-AAAL' as the key
* Complete the registration procedure

#### Multiplayer game
* Update your *settings* file by adding or changing the corresponding parameter to the server address, i.e. if you launch both the server and the game on the same machine, it will look this way:
    ```
    matchmaking_address=localhost
    ```
* Launch *servlets* module
* Launch *server* module
* Launch the game and choose *Mutiplayer* from the menu
* Create new account
* Create new profile

Note that all registrations made in the multiplayer will be lost once the *servlets* module is stopped.

Tribal Trouble (Original Readme)
==============
**The following is referenced from the [original repo](https://github.com/sunenielsen/tribaltrouble)**

Tribal Trouble is a realtime strategy game released by Oddlabs in 2004. In 2014 the source was released under GPL2 license, and can be found in this repository.

The source is released "as is", and Oddlabs will not be available for help building it, modifying it or any other kind of support. Due to the age of the game, it is reasonable to expect there to be some problems on some systems. Oddlabs has not released updates to the game for years, and do not intend to start updating it now that it is open sourced.

**If** you know how to code Java, configure ant, use MySQL, and have a **genuine intention** of actually working on the game, you can create an issue for detailed questions about the source.

Binaries
--------
If you are simply looking for a working binary version of the game, you can find the latest released installers here:

- [Windows](binaries/TribalTroubleSetup.exe)
- [Mac](binaries/TribalTrouble.dmg)
- [Linux](binaries/TribalTroubleSetup.sh)

Please note that the multiplayer server referenced in these builds, is no longer available.

You can register the binaries by putting the registration file in the binaries folder into this folder:
- Windows XP: `C:\Documents and Settings\Username\TribalTrouble\`
- Windows Vista or newer: `C:\Users\Username\TribalTrouble\`
- Mac OS X: `Library/Application Support/TribalTrouble/`
- Linux: `~/.TribalTrouble/`


Building
--------
Clone the repository:
```
git clone https://github.com/sunenielsen/tribaltrouble.git
```
Make sure you have Java SDK at least version 6, and Apache Ant.


To build the game client, do this:
```
cd tt
ant run
```

Setting up a server is a lot more complex, and not something we have done in many years. It will take some work to get it working, but try looking at the server folder and see if you can figure it out. At the very least, you should know a bit about setting up a MySQL server.
