Thanks for your contribution to [Apache Any23](https://any23.apache.org/)! Your help is appreciated!

Before opening the pull request, please verify that
* there is an open issue on the [Any23 issue tracker](https://issues.apache.org/jira/projects/ANY23) which describes the problem or the improvement. We cannot accept pull requests without an issue because the change wouldn't be listed in the release notes.
* the issue ID (`ANY23-XXXX`)
  - is referenced in the title of the pull request
  - and placed in front of your commit messages surrounded by square brackets (`[ANY23-XXXX] Issue or pull request title`)
* commits are squashed into a single one (or few commits for larger changes)
* Java source code follows [formatter-maven-plugin formatting rules](https://code.revelc.net/formatter-maven-plugin/)
* Any23 is successfully built and unit tests pass by running `mvn clean test`
* there should be no conflicts when merging the pull request branch into the *recent* master branch. If there are conflicts, please try to rebase the pull request branch on top of a freshly pulled master branch.

We will be able to faster integrate your pull request if these conditions are met. If you have any questions how to fix your problem or about using Any23 in general, please sign up for the [Any23 mailing list](http://any23.apache.org/mailing-lists.html). Thanks!
