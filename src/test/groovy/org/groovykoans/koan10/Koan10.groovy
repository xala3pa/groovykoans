/**
 * Copyright 2012-2013 The original author or authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.groovykoans.koan10

import groovy.xml.MarkupBuilder

/**
 * Koan10 - Slurpers and Builders
 *
 * Reading list:
 *  * http://groovy.codehaus.org/Reading+XML+using+Groovy's+XmlSlurper
 *  * http://groovy.codehaus.org/api/groovy/util/XmlSlurper.html
 *  * http://groovy.codehaus.org/groovy-jdk/java/util/Collection.html#sort(groovy.lang.Closure)
 *  * http://mrhaki.blogspot.com/2009/08/groovy-goodness-spaceship-operator.html
 *  * http://groovy.codehaus.org/Creating+XML+using+Groovy's+MarkupBuilder
 *  * http://groovy.codehaus.org/api/groovy/xml/MarkupBuilder.html
 *  * http://stackoverflow.com/questions/5936003/write-html-file-using-java
 *  * http://supportweb.cs.bham.ac.uk/docs/tutorials/docsystem/build/tutorials/ant/ant.html
 *  * http://groovy.codehaus.org/Using+Ant+from+Groovy
 *  * http://ant.apache.org/manual/Tasks/copy.html
 *  * http://ant.apache.org/manual/Tasks/checksum.html
 */
class Koan10 extends GroovyTestCase {

    void test01_XmlSlurpersReader() {
        // Time to learn about the built-in magic that Groovy brings to the table.
        // Using the reference at http://groovy.codehaus.org/Reading+XML+using+Groovy's+XmlSlurper and
        // http://groovy.codehaus.org/api/groovy/util/XmlSlurper.html, read the content of movies.xml
        // and find out how many movies are listed.
        def movieCount
        // ------------ START EDITING HERE ----------------------
	def records = new XmlSlurper().parse(new File('src/test/groovy/org/groovykoans/koan10/movies.xml'))
	movieCount = records.movie.size()

        // ------------ STOP EDITING HERE  ----------------------
        assert movieCount == 7

        // And now, return all the movie names that contain the word 'the' (case-insensitive)
        // Hint: pay attention to the type of objects you're getting.
        List<String> moviesWithThe = []
        // ------------ START EDITING HERE ----------------------
	 def filteredNodeChildren = records.movie.title.findAll{ it =~ '(?i).*the.*' }
	 moviesWithThe = filteredNodeChildren.collect() { it.text() } 	

        // ------------ STOP EDITING HERE  ----------------------
        assert moviesWithThe.containsAll(['Conan the Barbarian', 'The Expendables', 'The Terminator'])

        // How many movie ids have a value greater than 5?
        def movieIdsGreaterThan5
        // ------------ START EDITING HERE ----------------------
	movieIdsGreaterThan5 = records.movie.findAll { it.@id.text().toInteger() > 5 }.size()
        // ------------ STOP EDITING HERE  ----------------------
        assert movieIdsGreaterThan5 == 2
    }

    void test02_XmlSlurpersReader2() {
        // Using your new skills, return a list of movies, sorted by year (and alphabetically within the same year)
        // sort() and Spaceship operator can come in handy:
        // http://groovy.codehaus.org/groovy-jdk/java/util/Collection.html#sort(groovy.lang.Closure)
        // http://mrhaki.blogspot.com/2009/08/groovy-goodness-spaceship-operator.html

        List<String> sortedList = []
        // ------------ START EDITING HERE ----------------------
	def xml = new XmlSlurper().parse('src/test/groovy/org/groovykoans/koan10/movies.xml')
        def listOfNodeChildren = xml.movie.list().sort { node1, node2 ->
            def year1 = node1.year.text()
            def year2 = node2.year.text()
            return (year1 == year2) ? node1.title.text()<=>node2.title.text() : year1<=>year2
        }
        sortedList = listOfNodeChildren.collect { it.title.text() }	
	// ------------ STOP EDITING HERE  ----------------------
        assert sortedList == ['Conan the Barbarian', 'The Terminator', 'Predator',
                'Kindergarten Cop', 'Total Recall', 'True Lies', 'The Expendables']
    }

    void test03_XmlMarkupBuilder1() {
        // Groovy's MarkupBuilder allows you to create tree structures very easily.
        // Read here: http://groovy.codehaus.org/Creating+XML+using+Groovy's+MarkupBuilder
        // And here: http://groovy.codehaus.org/api/groovy/xml/MarkupBuilder.html

        // Let's try to create the following HTML in code:
        // <html>
        // <body>
        //    <h1>title</h1>
        // </body>
        // </html>

        // I'm not even going to try to do it the Java way, but it would have been something along
        // the lines of: http://stackoverflow.com/questions/5936003/write-html-file-using-java

        // Using MarkupBuilder, create the above html as String
        def html
        // ------------ START EDITING HERE ----------------------
	
	def writer = new StringWriter()
	def xml = new MarkupBuilder(writer)
	xml.html {
		body {
			h1 ( 'title' )
		}
	}
	html = writer.toString()
        // ------------ STOP EDITING HERE  ----------------------
        assert formatXml(html) == formatXml("<html><body><h1>title</h1></body></html>")
    }

    void test04_XmlMarkupBuilder2() {
        // Suppose we need to transform the movies.xml to a different format, as such:
        // <movies>
        //      <movie id='id' title='title' year='year'/>
        // </movies>

        // We could use XSLT... but MarkupBuilder makes it a breeze! Use it to
        // convert the movies.xml to the above format:

        String convertedXml
        // ------------ START EDITING HERE ----------------------
	def records = new XmlSlurper().parse(new File('src/test/groovy/org/groovykoans/koan10/movies.xml'))
	def writer = new StringWriter()
	def xml = new MarkupBuilder(writer)
	xml.movies {
		records.movie.each { node ->
			movie (id: node.@id.text(), title: node.title.text() ,year: node.year.text())
		}
	}
	convertedXml = writer.toString()
        // ------------ STOP EDITING HERE  ----------------------
        def expected = """|<movies>
                            |  <movie id='6' title='Total Recall' year='1990' />
                            |  <movie id='4' title='The Terminator' year='1984' />
                            |  <movie id='5' title='The Expendables' year='2010' />
                            |  <movie id='1' title='Conan the Barbarian' year='1982' />
                            |  <movie id='3' title='Predator' year='1987' />
                            |  <movie id='2' title='True Lies' year='1994' />
                            |  <movie id='7' title='Kindergarten Cop' year='1990' />
                            |</movies>""".stripMargin()

        assert formatXml(expected) == formatXml(convertedXml)
    }

    private String formatXml(String xml) {
        def stringWriter = new StringWriter()
        def node = new XmlParser().parseText(xml.toString());
        new XmlNodePrinter(new PrintWriter(stringWriter)).print(node)
        return stringWriter.toString()
    }

    void test05_AntBuilderCopy() {
        // Just in case you've never heard of Ant - it's a general purpose build tool. Many things can be said about
        // it, but nobody will deny its usefulness. It comes packed with an array of tasks, ranging from build,
        // file manipulation, communication, and others. Read the basics here:
        // http://supportweb.cs.bham.ac.uk/docs/tutorials/docsystem/build/tutorials/ant/ant.html

        // So how does Groovy support Ant? With AntBuilder: http://groovy.codehaus.org/Using+Ant+from+Groovy

        // Let's start by copying movies.xml to movies_copy.xml in the same directory using Ant's Copy task:
        // http://ant.apache.org/manual/Tasks/copy.html
        def baseDir = 'src/test/groovy/org/groovykoans/koan10'
        // ------------ START EDITING HERE ----------------------

	def ant = new AntBuilder()
	ant.copy(file:"${baseDir}/movies.xml", tofile:"${baseDir}/movies_copy.xml")
        // ------------ STOP EDITING HERE  ----------------------
        assert new File("${baseDir}/movies_copy.xml").exists()
    }

    void test06_AntBuilderChecksum() {
        // Ant uses properties (variables in Ant) extensively. Let's find out how.

        // Using the checksum Ant task, find out the checksum for movies.xml
        // http://ant.apache.org/manual/Tasks/checksum.html
        def baseDir = 'src/test/groovy/org/groovykoans/koan10'
        def actualChecksum
        // ------------ START EDITING HERE ----------------------
	def ant = new AntBuilder()
	ant.checksum(file: "${baseDir}/movies.xml", property: 'checksum')
	actualChecksum = ant.project.properties.checksum
        // ------------ STOP EDITING HERE  ----------------------
        assert actualChecksum == '9160b6a6555e31ebc01f30c1db7e1277'
    }

}
