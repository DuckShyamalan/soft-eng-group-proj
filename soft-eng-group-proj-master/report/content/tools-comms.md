# Tools and Communication

The first major issue we faced in completing a team project was version control.
We settled on *Git*[^git-lnk], since it has become more or less an industry standard over the past few years, and some of our team members had prior experience with it.

[^git-lnk]: https://git-scm.com/

Initially, some of our team members struggled with the steep learning curve, but thankfully more experienced members of the team were able to help them out, suggesting *SourceTree*[^source-tree-lnk]; a user-friendly Git GUI client, which the less experienced members were able to pick up quickly with help from the rest of the team.
While this took some time, managing a project of this complexity with 5 team members would have been near-impossible without a comprehensive version-control strategy, and is a valuable skill for all of us to have.
The distributed nature of Git also helped us avoid problems with downtime and equipment failure, so that we didn't lose work even when two of our team member's laptops failed.

[^source-tree-lnk]: https://www.sourcetreeapp.com/

We decided to use *IntelliJ IDEA*[^intellij-lnk] as our IDE, since it has an excellent developer tools feature set, including code analysis tools, built-in *JUnit*[^junit-lnk] support, and good integration with Git.
The JUnit support helped a lot with testing, allowing us to continuously run unit tests locally as we coded before committing and pushing changes, speeding up our workflow.
IntelliJ also supports Maven projects natively, so it was able to resolve build dependencies automatically, so the build didn't break on our machines every time a new dependency was added to the project.
We also made use of *LINQPad*[^linqpad-lnk]; a tool for querying data, which helped us ensure that our tests were comprehensive and that the data produced by our java code was correct.

[^intellij-lnk]: https://www.jetbrains.com/idea/
[^junit-lnk]: https://junit.org/
[^linqpad-lnk]: https://www.linqpad.net/

We hosted our codebase on *GitHub*[^github-lnk], since it integrates well with other industry standard services, has a good toolset, and provides many paid features to students for free, such as private repositories.
The service suffered a minor outage early on in a sprint, but other than this slight blip, we encountered no significant problems. 
GitHub also provided us with excellent pull request functionality, so we could enable code reviews so that no one person had exclusive information about any section of the code, and code was less buggy as a result.

[^github-lnk]: https://github.com/

Our Continuous Integration provider *Azure DevOps*[^azure-devops-lnk] was also very reliable and integrated well with GitHub, allowing us to automate the build-test process, track whether builds were passing or failing before merging pull requests, and trace bugs back to their source commit. 
It also helps to eliminate the many of the underlying issues that might cause the app to run on our development machines but not on the customer's machines, through the use of *Maven*[^maven-lnk] to manage java dependencies.

We also used Azure DevOps to keep track of our workload, utilising its support for the Scrum workflow, enabling us to keep track of User Story and task completion, with an excellent suite of time management tools (including a live-updated burndown chart).

[^azure-devops-lnk]: https://azure.microsoft.com/en-us/services/devops/
[^maven-lnk]: https://maven.apache.org/

We used a tool called *HackMD*[^hackmd-lnk] to keep track of our overarching plans through the entire project. This live collaboration tool was very useful in the planning phase, as we could easily all sit around a table and contribute to the same document simultaneously.

[^hackmd-lnk]: https://hackmd.io/

For documentation and presentation, we used the *Office365*[^office365-lnk] suite. The online live collaboration tools and built-in version control proved very useful when preparing for sprint review meetings, allowing us all to contribute to the documents and review the work of others in the team.

For the user manual, we used *Overleaf*[^overleaf-lnk]; a *LaTeX*[^latex-lnk] live collaboration editor. This allowed us to collaborate easily on the manual, without worrying too much about formatting, and produce a good-looking PDF with no 

[^office365-lnk]: https://www.office.com/
[^overleaf-lnk]: https://www.overleaf.com/
[^latex-lnk]: https://www.latex-project.org/

We initially communicated through *WhatsApp*[^whatsapp-lnk], but quickly decided to move to using *Slack*[^slack-lnk], since it provides notification integration with GitHub and Azure DevOps, making it easy to keep track of pull requests and builds.
But after a while of trying to switch, we found that, given the size of our team, we were better off with WhatsApp's simplicity, especially since we already use it for other communication, since the tools we were using already provided adequate ways of keeping tabs on things, such as the Azure DevOps Kanban Board and email notifications.
A larger team would likely have made use of other features of Slack, such as channels, which would likely have made it worth switching.

[^whatsapp-lnk]: https://www.whatsapp.com/
[^slack-lnk]: https://slack.com/intl/en-gb/?eu_nc=1

Email proved the best tool for keeping in contact with our supervisor, who preferred to use Outlook Calendar invitations to keep everyone on the same page for meetings.
This was an effective approach, automatically adding these events to our calendars so we were all on the same page.
We were able to book rooms for these meetings on campus through the University's online room booking system.

For Scrum stand-up meetings, the David Barron Computing Laboratory was an excellent space which we also used for collaborative development sessions, which we utilised to the fullest to improve code quality and spread expertise about various parts of the codebase around the team.
