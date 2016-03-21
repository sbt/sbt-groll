# sbt-groll #

Plugin for [sbt](http://www.scala-sbt.org) to "roll" – view and navigate – the [Git](http://git-scm.com/) commit history. This turns out to be very useful for live coding and training sessions. As of version 2 sbt-groll is using [JGit](http://www.eclipse.org/jgit/), i.e. no local Git installation is needed.

## Installing sbt-groll ##

sbt-groll is a plugin for sbt 0.13.11 or higher. In order to install sbt, please refer to the [sbt documentation](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html). Please make sure that you are using a suitable version of sbt:

As sbt-groll is a plugin for sbt, it is installed like any other sbt plugin, that is by mere configuration: just add sbt-groll to your global or local plugin definition. Global plugins are defined in `~/.sbt/<sbt_version>/plugins/plugins.sbt` and local plugins are defined in `project/plugins.sbt` in your project.

In order to add sbt-groll as a plugin, just add the below setting to the relevant plugin definition:

```
addSbtPlugin("de.heikoseeberger" % "sbt-groll" % "4.9.0")
```

After adding the sbt-groll plugin like this, you should either start sbt or, if it was already started, reload the current session by executing the `reload` command. If everything worked, you should have the new `groll` command available.

## Using sbt-groll ##

sbt-groll adds the `groll` command that provides various ways to view and navigate the Git history. Of course this means, that you can only use sbt-groll for projects which are using Git as version control system.

If you navigate the Git history, i.e. move to some commit, sbt-groll reloads the sbt session if the build definition changes, i.e. any `.sbt` file in the project root directory or any `.scala` or `.sbt` file in the `project/` directory.

In order to use sbt-groll, just execute `groll <arg_or_opt>` in an sbt session, giving one of the arguments or options described below. Here are two examples:

```
> groll show
[info] == 0bf1f60 Exercise: Connect to a remote system> groll move=bc1ac93
> groll head
[info] >> b97ef22 Exercise: HTTP server
```

## Settings ##

sbt-groll can be configured by the following settings:

- `grollConfigFile: java.io.File` – the configuration file for sbt-groll; "~/.sbt-groll.conf" by default
- `grollHistoryRef: String` – the ref (commit id, branch or tag) used for the Git history; "master" by default
- `grollWorkingBranch: String` – the working branch used by sbt-groll; "groll" by default

## Arguments/options ##

The `groll` command must be followed by one of the following arguments or options:

- `show` – shows the current commit id and message, if current commit is in history
- `list` – shows the full commit history
- `next` – moves to the next commit
- `prev` – moves to the previous commit
- `head` – moves to the head of the commit history
- `initial` – moves to a commit with a message containing "groll:initial" or starting with "Initial state" or with a tag "groll-initial"
- `move=<commit>` – moves to the given commit
- `push=<branch>` – pushes the current commit via HTTPS to the "origin-https" remote repository (needs to be defined!) under the given branch
- `version` – shows the version of sbt-groll
- `help` - shows this help info.

## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license. Whether or not you state this explicitly, by submitting any copyrighted material via pull request, email, or other means you agree to license the material under the project's open source license and warrant that you have the legal authority to do so.

## License ##

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
