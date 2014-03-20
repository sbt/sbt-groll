import sbtrelease.{ Version => ReleaseVersion, versionFormatError }

releaseSettings

ReleaseKeys.versionBump := sbtrelease.Version.Bump.Minor
