import java.io.*
import java.lang.StringBuilder
import kotlin.math.pow

fun main() {
    val n = 100
    val interval = listOf(1.0, 5.0)
    val grid = makeGrid(interval, n)

    val y0 = 1.0
    val values = fourthDegree(grid, y0, interval, n)
    val values2 = eulerMethod(grid, y0, interval, n)

    createGraphics(values, values2)
}

fun fourthDegree(grid: List<Double>, y0: Double, interval: List<Double>, n: Int): Map<Double, Double> {
    val values = HashMap<Double, Double>()
    val h = (interval.last() - interval.first()) / n

    for (x in grid) {
        var yPrev = y0

        if (x == interval.first()){
            values[x] = y0
        } else {
            val xN = x - h
            val yN = yPrev

            val f1 = f(xN, yN)
            val f2 = f(xN + h / 2, yN + h * f1 / 2)
            val f3 = f(xN + h / 2, yN + h * f2 / 2)
            val f4 = f(xN + h, yN + h * f3)

            values[x] = yN + h / 6 * (f1 + 2 * f2 + 2 * f3 + f4)
        }

        yPrev = values[x]!!
    }

    return values
}

fun eulerMethod(grid: List<Double>, y0: Double, interval: List<Double>, n: Int): Map<Double, Double> {
    val values = HashMap<Double, Double>()
    val h = (interval.last() - interval.first()) / n

    for (x in grid) {
        var yPrev = y0

        if (x == interval.first()) {
            values[x] = y0
        } else {
            val xN = x - h
            val yN = yPrev

            val f1 = f(xN, yN)
            val f2 = f(xN + h / 2, yN + f1 * h / 2 )

            values[x] = yN + h * f2
        }

        yPrev = values[x]!!
    }

    return values
}

fun makeGrid(interval: List<Double>, intervalsCnt: Int): List<Double> {
    val points = ArrayList<Double>()

    val startPoint = interval.first()
    val length = interval.last() - interval.first()
    var cnt = 1
    points.add(interval.first())

    do {
        val nextPoint = startPoint + length * cnt / (intervalsCnt)
        points.add(nextPoint)
        cnt += 1
    } while (nextPoint != interval.last())

    return points
}

fun f(x: Double, u: Double): Double {
    return (2 * x.pow(3)  + x.pow(2)) - u.pow(2) / (2 * x.pow(2) * u)
}

fun createGraphics(vararg values: Map<Double, Double>) {
    val templateReader = DataInputStream(FileInputStream("src/main/resources/template.html"))
    val html = File("src/main/resources/graphics.html")
    html.createNewFile()

    val htmlWriter = DataOutputStream(FileOutputStream(html))
    val htmlStr = String(templateReader.readAllBytes())

    val templateTable = "calculator.setExpression({type: 'table',columns" +
            ": [{latex: 'x',values: %x},{latex: 'y',values: %y,dragMode: Desmos.DragModes.XY},] });"

    val tablesStr = StringBuilder()
    for (value in values) {
        tablesStr.append(templateTable.replace("%x", value.keys.toString())
            .replace("%y", value.values.toString()))
    }

    val newStr = htmlStr.replace("%t", tablesStr.toString())
    htmlWriter.write(newStr.toByteArray())
}