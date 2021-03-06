/*******************************************************************************
 * Copyright 2012 Roland Ewald
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package sessl.james
import java.io.File
import sessl.AbstractObservation
import sessl.AbstractReport
import sessl.BoxPlotView
import sessl.DataView
import sessl.AbstractExperiment
import sessl.ExperimentResults
import sessl.HistogramView
import sessl.LinePlotView
import sessl.ReportNode
import sessl.ReportSection
import sessl.ReportSectionNode
import sessl.ScatterPlotView
import sessl.StatisticalTestView
import sessl.TableView
import org.jamesii.resultreport.ResultReportGenerator
import org.jamesii.resultreport.dataview.ScatterPlotDataView
import org.jamesii.resultreport.dataview.StatisticalTestDataView
import org.jamesii.resultreport.dataview.TableDataView
import org.jamesii.resultreport.ResultReportSection
import org.jamesii.resultreport.dataview.HistogramDataView
import org.jamesii.resultreport.dataview.BoxPlotDataView
import org.jamesii.resultreport.ResultReport
import org.jamesii.resultreport.dataview.LineChartDataView
import org.jamesii.resultreport.dataview.StatisticalTestDefinition
import org.jamesii.resultreport.renderer.rtex.RTexResultReportRenderer

/** Support for James II report generation.
 *
 *  @author Roland Ewald
 *
 */
trait Report extends AbstractReport {
  this: AbstractExperiment =>

  /** The result data views used in James II. */
  private type JDataView[D] = org.jamesii.resultreport.dataview.ResultDataView[D]

  override def generateReport(results: ExperimentResults) = {

    val report = new ResultReport(reportName, reportDescription)
    fillReportWithContent(report)

    // Check if directory exists
    val reportDir = new File(reportDirectory)
    if (!reportDir.exists())
      require(reportDir.mkdir(), "Could not create non-existent directory '" + reportDir.getAbsolutePath() + "'")

    // Generate report  
    (new ResultReportGenerator).generateReport(report, new RTexResultReportRenderer, new File(reportDirectory))
  }

  /** Fills the given report with content (by considering the data held in the super trait). */
  private[this] def fillReportWithContent(report: ResultReport) = {
    topmostElements.foreach(x => report.addSection(createSectionFromTopElem(x)))
  }

  /** Creates top-most elements of report (adds dummy sections to data views on top-most level). */
  def createSectionFromTopElem(node: ReportNode): ResultReportSection = node match {
    case section: ReportSectionNode => {
      val resultSection = new ResultReportSection(section.name, section.description)
      section.children.foreach(createRepresentationForElem(resultSection, _))
      resultSection
    }
    case view: DataView => {
      val resultSection = new ResultReportSection("Section for Dataview", "This section is automatically generated. Adding data views to the root section is not supported.")
      createRepresentationForElem(resultSection, view)
      resultSection
    }
    case _ => throw new IllegalArgumentException("Element " + node + " not supported.")
  }

  /** Creates the rest of the report hierarchy recursively. */
  def createRepresentationForElem(parent: ResultReportSection, node: ReportNode): Unit = node match {
    case section: ReportSectionNode => {
      val resultSection = new ResultReportSection(section.name, section.description)
      section.children.foreach(createRepresentationForElem(resultSection, _))
      parent.addSubSection(resultSection)
    }
    case view: DataView => {
      parent.addDataView(createJamesDataView(view))
      val x = 0
    }
    case _ => throw new IllegalArgumentException("Element " + node + " not supported.")
  }

  /** Creates data views. */
  def createJamesDataView(view: DataView): JDataView[_] = {
    import sessl.util.ScalaToJava._
    view match {
      case v: ScatterPlotView => new ScatterPlotDataView(to2DJavaDoubleArray(v.xData, v.yData),
        v.caption, v.title, Array[String](v.xLabel, v.yLabel))
      case v: HistogramView => new HistogramDataView(toDoubleArray(v.data), v.caption, v.title, v.xLabel, v.yLabel)
      case v: BoxPlotView => new BoxPlotDataView(to2DJavaDoubleArray(v.data.toSeq.map(_._2): _*),
        v.caption, v.title, Array(v.xLabel, v.yLabel), Array(v.data.toSeq.map(x => x._1): _*))
      case v: LinePlotView => new LineChartDataView(to2DJavaDoubleArray(v.data.toSeq.map(_._2): _*),
        v.caption, v.title, Array(v.xLabel, v.yLabel), Array(v.data.toSeq.map(x => x._1).tail: _*))
      case v: StatisticalTestView =>
        val dataPair = new org.jamesii.core.util.misc.Pair[Array[java.lang.Double], Array[java.lang.Double]](toDoubleArray(v.firstData._2), toDoubleArray(v.secondData._2))
        new StatisticalTestDataView(dataPair, v.caption, v.firstData._1, v.secondData._1, true, true, StatisticalTestDefinition.KOLMOGOROV_SMIRNOV)
      case v: TableView => new TableDataView(to2DTransposedJavaStringArray(v.data: _*), v.caption)
      case _ => throw new IllegalArgumentException("Data view " + view + " not yet supported.")
    }
  }

}
