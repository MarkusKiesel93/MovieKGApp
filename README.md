# Movie KG App

## Functionalities

### Basic operations

a) Automatically load an initial ontology from your local file system

When the application is started the movie.ttl file is loaded into the OntModel.
The movie.ttl has to be in the data folder where the .jar file is located.

b) Options for user to select and activate reasoners provided by the chosen APIs.

In the top panel "Basic operations" the user can select a reasoner type which will be applyed to the OntModel.

c) Export KG in a user-selected format

In the top panel "Basic operations" the user can select one of 4 formats to export the model.
The output file is saved in the data folder in the same dictionary as the -jar file as output.*

### Ontology operations

a) Add a new ontology class based on user inputs

In the mid panel "Ontology operations" the user can input a new OWL class with a chosen namespace. By default the "http://semantics.id/ns/example/movie#" is used.

b) Add a new property based on user inputs, including domain & range definition

In the mid panel "Ontology operations" the user can input a new object property. The domain and range of the property is checked if it already exists in the model.

c) Add ontology instances based on user input.

In the mid panel "Ontology operations" the user can input a new movie instance.
- The movie title is used as label and transformed to give the instance a proper name.
- Actors can be added by name, multiple actors can be added by separating them with ","
- A film studio can be added by name.
- Genres can be added by name, multiple genres can be add by separating them with ","
- Writers can be added by name, multiple writers can be added by separating them with ","
- A release date can be added in ISO format.

### Basic query operations

a) Show a list of queries that you have developed in Assignment 2 to users and allow
them to choose a query

One of 6 queries including SELECT, ASK, CONSTRUCT and DESCRIBE can be selected in the bottom panel "Basic query operations".

b) Execute the user-selected query on the ontology and show the query results

After selecting a query the "execute query" button in the bottom panel "Basic query operations" can be used to run the query and output the results to the "OUTPUT" panel.

## Example

![Alt text](Example_image.png?raw=true "Movie KG App")