# sbt-groll #

Plugin for [sbt](http://www.scala-sbt.org) to "roll" – view and navigate – the [Git](http://git-scm.com/) commit history. This turns out to be very useful for live coding and training sessions. As of version 2 sbt-groll is using [JGit](http://www.eclipse.org/jgit/), i.e. no local Git installation is needed.

## Installing sbt-groll ##

sbt-groll is a plugin for sbt 0.13. In order to install sbt, please refer to the [sbt documentation](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html). Please make sure that you are using a suitable version of sbt:

As sbt-groll is a plugin for sbt, it is installed like any other sbt plugin, that is by mere configuration: just add sbt-groll to your global or local plugin definition. Global plugins are defined in `~/.sbt/<SBT_VERSION>/plugins/plugins.sbt` and local plugins are defined in `project/plugins.sbt` in your project.

In order to add sbt-groll as a plugin, just add the below setting to the relevant plugin definition, paying attention to blank lines between settings:

```
addSbtPlugin("de.heikoseeberger" % "sbt-groll" % "4.1.0")
```

After adding the sbt-groll plugin like this, you should either start sbt or, if it was already started, reload the current session by executing the `reload` command. If everything worked, you should have the new `groll` command available.

## Using sbt-groll ##

sbt-groll adds the `groll` command that provides various options to view and navigate the Git history. Of course this means, that you can only use sbt-groll for projects which are using Git as version control system.

If you navigate the Git history, i.e. move to some commit, sbt-groll reloads the sbt session if the build definition changes.

In order to use sbt-groll, just execute `groll` in an sbt session, giving one of the options described below:

```
> groll show
[info] == 0bf1f60 Exercise: Connect to a remote system> groll move=bc1ac93
> groll head
[info] >> b97ef22 Exercise: HTTP server
```

## Settings ##

sbt-groll can be configured by the following settings:

- `configFile java.io.File`: The configuration file for sbt-groll; "~/.sbt-groll.conf" by default
- `historyRef: String`: The ref (commit id, branch or tag) used for the Git history; "master" by default
- `workingBranch: String`: The working branch used by sbt-groll; "groll" by default

## Options ##

The `groll` command must be followed by one of the following options:

- `show`: Shows the current commit id and message, if current commit is in history
- `list`: Shows the full commit history
- `next`: Moves to the next commit
- `prev`: Moves to the previous commit
- `head`: Moves to the head of the commit history
- `initial`: Moves to a commit with a message containing "groll:initial" or starting with "Initial state"
- `move=<commit>`: Moves to the given commit
- `pushSolutions`: Pushes the current commit to the "origin" remote repository under the branch `s"$historyRef-solutions"`, but only if `historyRef` matches the regex pattern `\d{8}-\w+-\w+
- `version`: Shows the version of sbt-groll

## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license. Whether or not you state this explicitly, by submitting any copyrighted material via pull request, email, or other means you agree to license the material under the project's open source license and warrant that you have the legal authority to do so.

## License ##

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
