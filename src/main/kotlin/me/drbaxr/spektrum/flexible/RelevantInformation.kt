package me.drbaxr.spektrum.flexible

import me.drbaxr.spektrum.fixed.model.HierarchyUnit
import me.drbaxr.spektrum.flexible.adapters.model.external.File
import me.drbaxr.spektrum.flexible.adapters.model.external.ImportModel
import me.drbaxr.spektrum.flexible.adapters.model.external.Project
import me.drbaxr.spektrum.flexible.identifiers.rules.cs.exceptions.*
import me.drbaxr.spektrum.flexible.identifiers.rules.cs.model.CSUnitInfo
import java.lang.Exception

class RelevantInformation {
    companion object {
        lateinit var importCSModel: ImportModel

        // returns all needed information about a C# method
        fun getCSImportModelInformation(unitIdentifier: String): CSUnitInfo {
            val splitId = unitIdentifier.split(HierarchyUnit.childSeparator) // file, namespace, class, method

            if (splitId.size != 4)
                throw NotHierarchyMethodException(unitIdentifier)

            var fileNullable: File? = null
            var projectNullable: Project? = null
            importCSModel.projects.forEach { prj ->
                val found = prj.files.find { it.path == splitId[0] }

                if (found != null) {
                    fileNullable = found
                    projectNullable = prj
                }
            }

            val file = fileNullable ?: throw FileNotFoundException(splitId[0])
            val project = projectNullable ?: throw Exception("Project was not found for $unitIdentifier")

            val namespace = file.namespaces.find { it.name == splitId[1] }
                ?: throw NamespaceNotFoundException(splitId[1])

            val cls = namespace.classes.find { it.name.split(".").last() == splitId[2] }
                ?: throw ClassNotFoundException(splitId[2])

            val method = cls.methods
                .find { it.name == splitId[3] } ?: throw MethodNotFoundException(splitId[3])

            return CSUnitInfo(
                project.getInfo(),
                file.getInfo(),
                namespace.getInfo(),
                cls.getInfo(),
                method.getInfo()
            )
        }
    }
}