import com.kgit2.kommand.process.Command
import com.kgit2.kommand.process.Stdio

class CommandExecutor {

    fun execute(command: String): String {
        val output = Command(command)
            .args()
            .stdout(Stdio.Pipe)
            .spawn()
            .waitWithOutput()

        return "Success!\n${output.stdout ?: "Empty output"}"
    }
}