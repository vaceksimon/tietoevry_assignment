# Assignment for internship “HTTP Log timeline visualizer”
This task was given to me as a part of the selection process for a summer internship.
Detailed information about the internship can be found [here](https://www.stazvit.cz/staze/http-log-timeline-visualizer/).

## Launch
Maven is set up to create a Java archive, but the application cannot be launched with
it on my machine, and unfortunately I was unable to fix it. So **I recommend using**
the `mvn spring-boot:run` command in the root of the directory (where the pom.xml is
located). The web application then starts and can be found at `http://127.0.0.1:8080/`.

## My approach
Since I have little to no experience with the Spring framework, I was looking for solutions
using the Model-View-Controller architecture. It is popular with web applications, and I am
quite familiar with it in JavaFX applications and a bit in the PHP Symfony framework.
\
In this project I am using the Vaadin platform as it makes dynamically changing html elements
and routing a lot easier. I am not sure if it is too big of a cheat, but I tried my best.
<p>My solution is not perfect because the whole code is written in a single file *MainView*.
Because of the small size of this project, it does not matter that much, but in larger applications,
I would look more into how Springboot works.</p> 