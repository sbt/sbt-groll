G(it)Roll
=========

Plugin for `sbt`_ to view and navigate through the `Git`_ history.


Installing groll
----------------

groll is a plugin for `sbt`_ 0.12. Please make sure that you are using an appropriate sbt release. In order to download and install sbt, please refer to the `sbt Getting Started Guide / Setup`_. Additionally groll requires a `Git`_ installation.

As groll is a plugin for sbt, it is installed like any other sbt plugin, that is by mere configuration. For details about using sbt plugins, please refer to the `sbt Getting Started Guide / Using Plugins`_. 

Most probably you can skip the details and just add groll to your global or local plugin definition. Global plugins are defined in a *plugins.sbt* file in the *~/.sbt/plugins/* directory and local plugins are defined in a *plugins.sbt* file in the *project/* folder of your project. 

In order to add groll, just add the below setting to the relevant plugin definition, paying attention to blank lines between settings::

  addSbtPlugin("name.heikoseeberger.groll" % "groll" % "1.3.0")

After adding the groll plugin like this, you should either start sbt or, if it was already started, reload the current session by executing the *reload* command. If everything worked, you should have the new command *groll* available.


Using groll
-----------

Groll provides the command *groll* that provides various options to view and navigate through the Git history. Of course this means, that you can only use groll for projects using Git as version control system. If you are navigating through the Git history, groll will reload the sbt session if the build definition changed.

In order to use groll, just execute the command *groll* in an sbt session, giving one of the options described below::

  > groll show
  [info] Current commit: 534ef78 Exercise: Add XML serialization to Time
  >
  > groll move=bc1ac93
  [info] Moved to commit: bc1ac93

Settings
--------

Groll can be configured by the following settings:

- *postCommands: Seq[String]* - The commands to be executed after grolling. If not defined, the empty sequence is used.
- *revision: String* - The revision (branch or tag) used for the Git history. If not defined, "master" is used.


Options
-------

The command *groll* must be followed by one of the following options:

- *show*: Shows the current commit id and message, if current commit is in history
- *list*: Shows all commits
- *next*: Moves to the next commit, reloading the sbt session if the build definition changed, if current commit is in history
- *prev*: Moves to the previous commit, reloading the sbt session if the build definition changed, if current commit is in history
- *head*: Moves to the head of the commit history, reloading the sbt session if the build definition changed
- *move*: Moves to the given commit, reloading the sbt session if the build definition changed


Contribution policy
-------------------

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license. Whether or not you state this explicitly, by submitting any copyrighted material via pull request, email, or other means you agree to license the material under the project's open source license and warrant that you have the legal authority to do so.


License
-------

This code is open source software licensed under the `Apache 2.0 License`_. Just use it, if you like it.


.. _`sbt`: http://github.com/harrah/xsbt/
.. _`Git`: http://git-scm.com/
.. _`sbt Getting Started Guide / Setup`: http://github.com/harrah/xsbt/wiki/Getting-Started-Setup
.. _`sbt Getting Started Guide / Using Plugins`: http://github.com/harrah/xsbt/wiki/Getting-Started-Using-Plugins
.. _`Apache 2.0 License`: http://www.apache.org/licenses/LICENSE-2.0.html
