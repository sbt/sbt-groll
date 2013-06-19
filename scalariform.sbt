import scalariform.formatter.preferences.AlignSingleLineCaseStatements

scalariformSettings

ScalariformKeys.preferences <<= ScalariformKeys.preferences(_.setPreference(AlignSingleLineCaseStatements, true))
