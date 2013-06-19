sbt-groll
=========

Plugin for [sbt](http://www.scala-sbt.org) to "roll" – view and navigate – the [Git](http://git-scm.com/) commit history. This turns out to be very useful for live coding and training sessions.

Installing sbt-groll
--------------------

sbt-groll is a plugin for sbt. In order to install sbt, please refer to the [sbt documentation](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html). Please make sure that you are using a suitable version of sbt: 

- sbt-groll 1.5 → sbt 0.12
- sbt-groll 1.6 → sbt 0.13

Additionally sbt-groll requires a local Git installation. In order to install Git, please check out the [Git downloads page](http://git-scm.com/download) or use a package manager like [Homebrew](http://mxcl.github.io/homebrew/) for Mac OS.

As sbt-groll is a plugin for sbt, it is installed like any other sbt plugin, that is by mere configuration: just add sbt-groll to your global or local plugin definition. Global plugins are defined in `~/.sbt/<SBT_VERSION>/plugins/plugins.sbt` and local plugins are defined in `project/plugins.sbt` in your project. 

In order to add sbt-groll as a plugin, just add the below setting to the relevant plugin definition, paying attention to blank lines between settings:

```
addSbtPlugin("name.heikoseeberger" % "sbt-groll" % "1.6.0")
```

After adding the sbt-groll plugin like this, you should either start sbt or, if it was already started, reload the current session by executing the `reload` command. If everything worked, you should have the new `groll` command available.

Using sbt-groll
---------------

sbt-groll provides the `groll` command that provides various options to view and navigate the Git history. Of course this means, that you can only use sbt-groll for projects which are using Git as version control system.

If you are navigating through the Git history, i.e. moving to some commit, sbt-groll will reload the sbt session if the build definition changed.

In order to use sbt-groll, just execute `groll` in an sbt session, giving one of the options described below:

```
> groll show
[info] Current commit: 534ef78 Exercise: Add XML serialization to Time
> groll move=bc1ac93
[info] Moved to commit: bc1ac93
```

Settings
--------

sbt-groll can be configured by the following settings:

- `postCommands: Seq[String]`: The commands to be executed after "rolling". If not defined, the empty sequence is used, i.e. not other commands are executed.
- `revision: String`: The revision (branch or tag) used for the Git history. If not defined, "master" is used.

Options
-------

The `groll` command must be followed by one of the following options:

- `show`: Shows the current commit id and message, if current commit is in history
- `list`: Shows all commits
- `next`: Moves to the next commit, reloading the sbt session if the build definition has changed, if current commit is in history
- `prev`: Moves to the previous commit, reloading the sbt session if the build definition has changed, if current commit is in history
- `head`: Moves to the head of the commit history, reloading the sbt session if the build definition has changed
- `initial`: Moves to a commit with message "Initial state", reloading the sbt session if the build definition has changed
- `move`: Moves to the given commit, reloading the sbt session if the build definition has changed

Contribution policy
-------------------

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license. Whether or not you state this explicitly, by submitting any copyrighted material via pull request, email, or other means you agree to license the material under the project's open source license and warrant that you have the legal authority to do so.

License
-------

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
