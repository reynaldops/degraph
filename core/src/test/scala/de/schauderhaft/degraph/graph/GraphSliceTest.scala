package de.schauderhaft.degraph.graph

import org.scalatest.FunSuite
import org.scalatest.Matchers._
import scalax.collection.{ Graph => SGraph }
import de.schauderhaft.degraph.slicer.PackageCategorizer
import de.schauderhaft.degraph.model.SimpleNode._
import scalax.collection.edge.Implicits._
import de.schauderhaft.degraph.slicer.MultiCategorizer
import de.schauderhaft.degraph.slicer.InternalClassCategorizer
import de.schauderhaft.degraph.model.{Node, SimpleNode}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scalax.collection.{ Graph => SGraph }
import scalax.collection.edge.LkDiEdge

@RunWith(classOf[JUnitRunner])
class GraphSliceTest extends FunSuite {

  test("the package slice of a graph without package is empty") {
    val g = new Graph()
    g.add(SimpleNode("x", "x"))

    g.slice(packageType) should be(SGraph[Node, LkDiEdge]())
  }

  test("the package slice of a graph with some nodes in a single package is that package") {
    val g = new Graph(category = PackageCategorizer)
    g.add(classNode("p.C"))

    g.slice(packageType) should be(SGraph[Node, LkDiEdge](packageNode("p")))
  }

  test("the package slice of a graph with two connected nodes in two packages will be that two packages connected") {
    val g = new Graph(category = PackageCategorizer)
    g.connect(classNode("p.one.Class"), classNode("p.two.Class"))

    g.slice(packageType) should be(SGraph((packageNode("p.one") ~+#> packageNode("p.two"))(Graph.references)))
  }

  test("test non existing slice") {
    val g = new Graph(category = PackageCategorizer)
    g.connect(classNode("p.one.Class"), classNode("p.two.Class"))

    g.slice("no such type") should be(SGraph[Node, LkDiEdge]())
  }

  test("the package slice of an inner class is its package") {
    // since the slice node will appear anyway we use an edge between to inner classes, to test that they get projected on the correct slice
    val g = new Graph(category = MultiCategorizer.combine(InternalClassCategorizer, PackageCategorizer))

    g.connect(classNode("p.one.Class$Inner"), classNode("p.two.Class$Inner"))

    g.slice(packageType) should be(SGraph((packageNode("p.one") ~+#> packageNode("p.two"))(Graph.references)))
  }

}