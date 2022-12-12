package com.semanticversion

open class Version {

    companion object {
        const val VERSION_CLASSIFIER_SEPARATOR = "-"
        const val SNAPSHOT_CLASSIFIER = "SNAPSHOT"
        const val BETA_CLASSIFIER = "BETA"
        const val ALPHA_CLASSIFIER = "ALPHA"
        const val RC_CLASSIFIER = "RC"
        const val BASE_VERSION_SEPARATOR = "."
        // const val LOCAL_CLASSIFIER = "LOCAL"
        // const val VERSION_TIMESTAMP_FORMAT = "YYYYMMddHHmmss"
    }

    var versionMajor: Int? = null
    var versionMinor: Int? = null
    var versionPatch: Int? = null
    var versionReleaseCandidate: Int? = null
    var versionClassifier: String? = null
    var isSnapshot: Boolean = true
    var isBeta: Boolean = false
    var isAlpha: Boolean = false
    var isReleaseCandidate: Boolean = false

    // TODO Add support to this
    // var isVersionTimestampEnabled: Boolean = false
    // var isLocal: Boolean = false
    // var featureName: String? = null
    // var featureBranchPrefix: String? = null

    var maximumVersion: Int?

    protected open val defaultMaximumVersion: Int
        get() = 99

    val baseVersion: String
        get() = versionMajor.toString() + BASE_VERSION_SEPARATOR + versionMinor + BASE_VERSION_SEPARATOR + versionPatch

    constructor(versionMajor: Int, versionMinor: Int, versionPatch: Int) {
        maximumVersion = defaultMaximumVersion
        this.versionMajor = versionMajor
        this.versionMinor = versionMinor
        this.versionPatch = versionPatch
        validateBaseVersion()
    }

    constructor(versionMajor: Int, versionMinor: Int, versionPatch: Int, versionReleaseCandidate: Int) {
        maximumVersion = defaultMaximumVersion
        this.versionMajor = versionMajor
        this.versionMinor = versionMinor
        this.versionPatch = versionPatch
        this.versionReleaseCandidate = versionReleaseCandidate
        validateBaseVersion()
    }

    constructor(version: String) {
        maximumVersion = defaultMaximumVersion
        val split = version.split(VERSION_CLASSIFIER_SEPARATOR)
        if (split.size > 1) {
            parseBaseVersion(split[0])
            versionClassifier = split[1]
            parseVersionClassifier(versionClassifier!!)
            // isLocal = versionClassifier == LOCAL_CLASSIFIER
            // isVersionTimestampEnabled = false
        } else {
            if (version.contains(RC_CLASSIFIER)) {
                parseBaseVersion(version)
            } else {
                parseBaseVersion(split[0])
                isSnapshot = false
                isAlpha = false
                isBeta = false
                isReleaseCandidate = false
                // isLocal = false
                // isVersionTimestampEnabled = false
            }
        }
    }

    constructor(baseVersion: String, config: SemanticVersionConfig) {
        maximumVersion = config.maximumVersion ?: defaultMaximumVersion
        parseBaseVersion(baseVersion)

        versionClassifier = config.versionClassifier
        if (versionClassifier == null) {
            // featureBranchPrefix = config.featureBranchPrefix
            // if (!featureBranchPrefix.isNullOrEmpty()) {
            //     val gitBranch = gitHelper.getGitBranch()
            //     val isFeatureBranch = gitBranch?.startsWith(featureBranchPrefix!!) ?: false
            //     if (isFeatureBranch) {
            //         featureName = gitBranch!!.replace(featureBranchPrefix!!, "")
            //         versionClassifier = featureName
            //     }
            // }
            //
            // config.local?.let {
            //     isLocal = it
            // }
            // if (isLocal) {
            //     if (versionClassifier == null) {
            //         versionClassifier = ""
            //     } else {
            //         versionClassifier += VERSION_CLASSIFIER_SEPARATOR
            //     }
            //     versionClassifier += LOCAL_CLASSIFIER
            // }
            //
            // config.versionTimestampEnabled?.let {
            //     isVersionTimestampEnabled = it
            // }
            // if (isVersionTimestampEnabled) {
            //     if (versionClassifier == null) {
            //         versionClassifier = ""
            //     } else {
            //         versionClassifier += VERSION_CLASSIFIER_SEPARATOR
            //     }
            //     versionClassifier += format(now(), VERSION_TIMESTAMP_FORMAT)
            // }

            if (config.alpha == true) {
                versionClassifier = ALPHA_CLASSIFIER

                isAlpha = true
                isBeta = false
                isSnapshot = false
                isReleaseCandidate = false
            } else {
                if (config.beta == true) {
                    versionClassifier = BETA_CLASSIFIER

                    isAlpha = false
                    isBeta = true
                    isSnapshot = false
                    isReleaseCandidate = false
                } else {
                    if (config.snapshot == true) {
                        versionClassifier = SNAPSHOT_CLASSIFIER

                        isAlpha = false
                        isBeta = false
                        isSnapshot = true
                        isReleaseCandidate = false
                    } else {
                        if (config.releaseCandidate == true) {
                            isAlpha = false
                            isBeta = false
                            isSnapshot = false
                            isReleaseCandidate = true
                        } else {
                            isAlpha = false
                            isBeta = false
                            isSnapshot = false
                            isReleaseCandidate = false
                        }
                    }
                }
            }
        } else {
            parseVersionClassifier(versionClassifier!!)
            // isLocal = false
            // isVersionTimestampEnabled = false
        }
    }

    private fun parseVersionClassifier(versionClassifier: String) {
        when (versionClassifier) {
            ALPHA_CLASSIFIER -> {
                isSnapshot = false
                isBeta = false
                isAlpha = true
                isReleaseCandidate = false
            }
            BETA_CLASSIFIER -> {
                isSnapshot = false
                isBeta = true
                isAlpha = false
                isReleaseCandidate = false
            }
            SNAPSHOT_CLASSIFIER -> {
                isSnapshot = true
                isBeta = false
                isAlpha = false
                isReleaseCandidate = false
            }
            RC_CLASSIFIER -> {
                isSnapshot = false
                isBeta = false
                isAlpha = false
                isReleaseCandidate = true
            }
            else -> {
                isSnapshot = false
                isBeta = false
                isAlpha = false
                isReleaseCandidate = false
            }
        }
    }

    private fun parseBaseVersion(baseVersion: String) {
        val versionSplit = baseVersion.split(BASE_VERSION_SEPARATOR)
        if (versionSplit.size < 3 || versionSplit.size > 5) {
            throw RuntimeException("The version [$baseVersion] is not a valid Semantic Versioning")
        }

        if (versionSplit.size == 3) {
            versionMajor = versionSplit[0].toInt()
            versionMinor = versionSplit[1].toInt()
            versionPatch = versionSplit[2].toInt()
        } else if (versionSplit.size == 5) {
            versionMajor = versionSplit[0].toInt()
            versionMinor = versionSplit[1].toInt()
            versionPatch = versionSplit[2].toInt()
            versionReleaseCandidate = versionSplit[4].toInt()
        }

        validateBaseVersion()
    }

    private fun validateBaseVersion() {
        if (versionMajor!! > maximumVersion!! || versionMajor!! < 0) {
            throw RuntimeException("The version major [$versionMajor] should be a number between 0 and $maximumVersion")
        }
        if (versionMinor!! > maximumVersion!! || versionMinor!! < 0) {
            throw RuntimeException("The version minor [$versionMinor] should be a number between 0 and $maximumVersion")
        }
        if (versionPatch!! > maximumVersion!! || versionPatch!! < 0) {
            throw RuntimeException("The version patch [$versionPatch] should be a number between 0 and $maximumVersion")
        }
    }

    fun incrementMajor() {
        resetReleaseCandidate()
        if (versionMajor!! < maximumVersion!!) {
            versionMajor = versionMajor!! + 1
            versionMinor = 0
            versionPatch = 0
        } else {
            throw RuntimeException("The version major [$versionMajor] can't be incremented. Maximum value achieved.")
        }
    }

    fun incrementMinor() {
        resetReleaseCandidate()
        if (versionMinor!! < maximumVersion!!) {
            versionMinor = versionMinor!! + 1
            versionPatch = 0
        } else {
            incrementMajor()
        }
    }

    fun incrementPatch() {
        resetReleaseCandidate()
        if (versionPatch!! < maximumVersion!!) {
            versionPatch = versionPatch!! + 1
        } else {
            incrementMinor()
        }
    }

    fun promoteReleaseCandidate() {
        resetReleaseCandidate()
    }

    private fun resetReleaseCandidate() {
        versionReleaseCandidate = null
    }

    fun incrementReleaseCandidate() {
        if (versionReleaseCandidate != null) {
            versionClassifier = RC_CLASSIFIER
            versionReleaseCandidate = versionReleaseCandidate!! + 1
        } else {
            versionClassifier = RC_CLASSIFIER
            versionReleaseCandidate = 0
        }
    }

    override fun toString(): String {
        var versionName = baseVersion
        if (!versionClassifier.isNullOrEmpty()) {

            if (versionClassifier != RC_CLASSIFIER) {
                versionName += "$VERSION_CLASSIFIER_SEPARATOR$versionClassifier"
            } else {
                versionName += "$BASE_VERSION_SEPARATOR$RC_CLASSIFIER$BASE_VERSION_SEPARATOR$versionReleaseCandidate"
            }
        }
        return versionName
    }
}
