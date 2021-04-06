# Overview

From the two options provided, our team has decided to work on the Ad Auction Dashboard project.
After a semester of hard work, we have created an application that fulfils all the basic requirements, as well as some of the optional ones, as defined in the *project definition document*[^document-lnk].

[^document-lnk]: https://secure.ecs.soton.ac.uk/noteswiki/images/SEG_Ad_Auction_Dashboard_Definition_2015.pdf

The application itself was delivered on time and fully meets the required criteria. 
From creating custom queries with toggleable filters and granularity to saving the current state of the workspace, the software that was delivered even implements extension goals, going above and beyond providing the minimum functionality specified. 
One notable extension goal that our project implements is the ability to save charts as PNG files, which we implemented by allowing the user to interact with Chart Panel objects directly.

Due to modularity and maintenance being our top priorities, the backend for the queries uses a Query Engine specifically designed for this project.
The query engine is extremely powerful and modular. One of the things it allowed us to do is to save the state of the workspace to a file (the extension we have chosen being ADC), and then recreate it when the user chooses to load it.
What's more, because the workspace is replicated, the user does not have to wait for all the queries to be recreated; they can start using the software right away. 
Our team made sure to implement the MVC (Model View Controller) architectural pattern when designing the system so that the different aspects of the app could be developed in relative isolation, avoid strange sideffects, so the backend could be optimised without having to worry about any effects it might have on the GUI.

Initially, this inspired us to split into subgroups to develop each portion of the app, but we quickly realised this wasn't productive, as it caused some of the functionality implemented on the backend not to be fit for purpose, since the backend developers didn't properly understand what functionality they needed to provide.
We quickly found that a more relaxed splitting was appropriate, where we all met and worked together on planning and development, even though we tended to specialise individually.
This meant that we all had a hand in development across the app, and so understood the architecture well and were all able to produce functionality that fitted nicely together.

Regular meetings also inspired increased pair programming, and better communication and understanding across the board.
This way we could divide the work more evenly so that no one person had too much or too little work, yet everyone had someone they could turn to for help. 
Pair programming also helped us to work around our (at times) busy schedules as the people paired together on a specific piece of functionality could meet in their own time regardless of what the others were doing if need be.
Splitting our app down into these small 'tasks' allowed us to work more flexibly when needed, and our online tooling meant that everyone knew exactly what they needed to do, who was working on what and how far away they were from their goal.

Whenever we reached an important milestone in our work, we contacted the client and asked for a review of what we have achieved. This feedback was important in ensuring we remained on the right path, and that the deliverable would meet the client's expectations. Typically, a meeting was arranged, and we could explain in person what we had done, as well as discuss how we should proceed. Should our schedules not permit this, we instead communicated via email to ensure that everybody was kept up to date with the latest developments.

We feel we refined our approach well over the course of the project, and we believe that the Agile methods and techniques we picked up had a strong positive impact on the quality of our work.
