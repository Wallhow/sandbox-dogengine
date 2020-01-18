package dogengine.scripts

import com.badlogic.gdx.utils.ArrayMap
import java.io.File
import java.io.PrintWriter


class ConverterResourcesToClass {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            typeFiles.add("atlas")
            typeFiles.add("ttf")
            typeFiles.add("tmx")
            typeFiles.add("glsl")
            typeFiles.add("part")
            typeFiles.add("png")
            ConverterResourcesToClass().generate(path)
        }
        var path = "C://Users/Андрей/IdeaProjects/sandbox-dogengine/assets"
        var outputDirectory = "C://Users/Андрей/IdeaProjects/sandbox-dogengine/core/src/main/kotlin/"
        val packageDef = "sandbox"
        val typeFiles: ArrayList<String> = ArrayList()
    }

    fun generate(path: String,nameClass: String = "R") {
        val myFolder = File(path)
        val files = myFolder.listFiles()
        val arrayFiles: ArrayList<Field> = ArrayList()
        println("enter to directory "+ myFolder.path)
        recReadDirectory(files,arrayFiles)
        arrayFiles.forEach {
            println(it.toString())
        }

        var classBody = "package $packageDef\n"+
                "object $nameClass{\n"
        arrayFiles.forEach {
            classBody+=it.toString()+"\n"
        }
        classBody+="}"
        val dest = File("$outputDirectory\\$nameClass.kt")
        val out = PrintWriter(dest)
        out.use { out ->
            // response is the data written to file
            out.print(classBody)
        }
    }

    private fun recReadDirectory(files: Array<File>, output: ArrayList<Field>) {
        val names : ArrayMap<String,Int> = ArrayMap()
        files.forEach {file ->
            if (file.isDirectory) {
                if(file.listFiles()!=null)
                    recReadDirectory(file.listFiles(), output)
            } else if (file.isFile) {
                val point = file.name.lastIndexOf('.')+1
                typeFiles.forEach { it ->
                    val fileExtension =  file.name.substring(point,file.name.lastIndex+1)
                    println("extension file $fileExtension")
                    if(it == fileExtension) {
                        val name = file.name.substring(0,file.name.lastIndexOf('.'))
                        println(file.canonicalPath)
                        val finalDir = ConverterResourcesToClass.path.substring(
                                ConverterResourcesToClass.path.lastIndexOf('/')+1,
                                ConverterResourcesToClass.path.lastIndex+1)
                        val path = file.path.substring(file.path.lastIndexOf(finalDir)).replace('\\','/')

                        if(names[name]==null) {
                            names.put(name,0)
                        }
                        //Проверка на одинаковое имя
                        output.forEach {field ->
                            if (field.name == name+names[name]) {
                                names.put(name, names[name] + 1)
                            }
                        }

                        output.add(Field(name+names[name], path))

                    }
                }

            }
        }
    }

    data class Field(val name: String,val path: String) {
        override fun toString(): String {
            return "val $name = \"$path\""
        }
    }


}