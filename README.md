<img src="images/frites_text.png" alt="Frites Logo">

## Welcome !

This is the codebase for the FTC team FRITES (20991 from France) during the 2025-26 Decode season.

## Setting up & Compiling

Start by cloning this repository with one of the following commands:

```bash
git clone https://gitlab.com/ftc-civ/frites/2026.git
git clone git@gitlab.com:ftc-civ/frites/2026.git
```

Make sure you have [Android Studio](https://developer.android.com/studio) installed (this is
required to compile the code), and Java 21 (may not work with more recent versions!).

Open this directory in Android Studio and let it sync (required if you want to be able to compile
the code, even from the terminal).

To compile, use Android Studio or run the following command (on Linux, you may have to run
`chmod +x ./gradlew` first):

```bash
./gradlew build
```

## Robot manual

Detailed instructions, schematics, and everything you need to know about the robot are available
at [docs/manual.pdf](docs/manual/manual.pdf). Please note that the information provided there is
exclusively in French and might be outdated.

## Coding guidelines

### Formatting

- __Classes and enums__ use _PascalCase_
- __Objects, variables and functions__ use _camelCase_
- __Constants and enum members__ use _CONSTANT_CASE_
- Indent _4_ spaces
- Brackets are on the same line as the clause
- Split up long lines
- **Use a formatter**

```java
class Class { /*...*/
}

enum Enum {
    FIRST_MEMBER,
    SECOND_MEMBER
}

int exampleVariable;

public void exampleFunction() { /*...*/ }
``` 

## License

This program is licensed under the GPLV3 license.

See [LICENSE](LICENSE).