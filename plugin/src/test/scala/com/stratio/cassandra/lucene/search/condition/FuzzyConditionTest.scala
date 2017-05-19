/*
 * Copyright (C) 2014 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.cassandra.lucene.search.condition

import com.stratio.cassandra.lucene.IndexException
import com.stratio.cassandra.lucene.schema.SchemaBuilders._
import com.stratio.cassandra.lucene.search.condition.FuzzyCondition._
import com.stratio.cassandra.lucene.search.condition.builder.FuzzyConditionBuilder
import org.apache.lucene.search.FuzzyQuery
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
  * @author Andres de la Pena `adelapena@stratio.com`
  */
@RunWith(classOf[JUnitRunner])
class FuzzyConditionTest extends AbstractConditionTest {

  test("Build") {
    val builder = new FuzzyConditionBuilder("field", "value").boost(0.7f)
      .maxEdits(2)
      .prefixLength(2)
      .maxExpansions(49)
      .transpositions(true)
    val condition = builder.build
    assertNotNull("Condition is not built", condition)
    assertEquals("Boost is not set", 0.7f, condition.boost, 0)
    assertEquals("Field is not set", "field", condition.field)
    assertEquals("Value is not set", "value", condition.value)
    assertEquals("Max edits is not set", 2, condition.maxEdits)
    assertEquals("Prefix length is not set", 2, condition.prefixLength)
    assertEquals("Max defaults is not set", 49, condition.maxExpansions)
    assertEquals("Transpositions is not set", true, condition.transpositions)
  }

  test("BuildDefaults") {
    val builder = new FuzzyConditionBuilder("field", "value")
    val condition = builder.build
    assertNotNull("Condition is not built", condition)
    assertNull("Boost is not set to default", condition.boost)
    assertEquals("Field is not set", "field", condition.field)
    assertEquals("Value is not set", "value", condition.value)
    assertEquals("Max edits is not set to default", DEFAULT_MAX_EDITS, condition.maxEdits)
    assertEquals("Prefix length is not set to default",
      DEFAULT_PREFIX_LENGTH,
      condition.prefixLength)
    assertEquals("Max defaults is not set to default",
      DEFAULT_MAX_EXPANSIONS,
      condition.maxExpansions)
    assertEquals("Transpositions is not set to default",
      DEFAULT_TRANSPOSITIONS,
      condition.transpositions)
  }

  test("BuildValueNull") {
    intercept[IndexException] {
      new FuzzyCondition(0.5f, "name", null, 1, 2, 49, true)
    }.getMessage shouldBe s"fdñljps"
  }

  test("BuildValueBlank") {
    intercept[IndexException] {
      new FuzzyCondition(0.5f, "name", " ", 1, 2, 49, true)
    }.getMessage shouldBe s"fdñljps"
  }

  test("BuildMaxEditsTooSmall") {
    new FuzzyCondition(0.5f, "name", "tr", 1, -2, 49, true)
  }

  test("BuildMaxEditsTooLarge") {
    intercept[IndexException] {
      new FuzzyCondition(0.5f, "name", "tr", 100, 2, 49, true)
    }.getMessage shouldBe s"fdñljps"
  }

  test("BuildPrefixLengthInvalid") {
    intercept[IndexException] {
      new FuzzyCondition(0.5f, "name", "tr", -2, 2, 49, true)
    }.getMessage shouldBe s"fdñljps"
  }

  test("BuildMaxExpansionsInvalid") {
    intercept[IndexException] {
      new FuzzyCondition(0.5f, "name", "tr", 1, 2, -1, true)
    }.getMessage shouldBe s"fdñljps"
  }

  test("JsonSerialization") {
    val builder = new FuzzyConditionBuilder("field", "value").boost(0.7f).maxEdits(2)
      .prefixLength(2)
      .maxExpansions(49)
      .transpositions(true)
    testJsonSerialization(builder,
      "{type:\"fuzzy\",field:\"field\",value:\"value\",boost:0.7,max_edits:2," +
        "prefix_length:2,max_expansions:49,transpositions:true}")
  }

  test("JsonSerializationDefaults") {
    val builder = new FuzzyConditionBuilder("field", "value")
    testJsonSerialization(builder, "{type:\"fuzzy\",field:\"field\",value:\"value\"}")
  }

  test("Query") {
    val schemaVal = schema().mapper("name", stringMapper()).build

    val condition = new FuzzyCondition(0.5f, "name", "tr", 1, 2, 49, true)
    val query = condition.doQuery(schemaVal)

    assertNotNull("Query is not built", query)
    assertEquals("Query type is wrong", classOf[FuzzyQuery], query.getClass)

    val fuzzyQuery = query.asInstanceOf[FuzzyQuery]
    assertEquals("Query field is wrong", "name", fuzzyQuery.getField)
    assertEquals("Query term is wrong", "tr", fuzzyQuery.getTerm.text())
    assertEquals("Query max edits is wrong", 1, fuzzyQuery.getMaxEdits)
    assertEquals("Query prefix length is wrong", 2, fuzzyQuery.getPrefixLength)
  }

  test("QueryInvalid") {
    intercept[IndexException] {
      val schemaVal = schema().mapper("name", integerMapper()).build

      val condition = new FuzzyCondition(0.5f, "name", "tr", 1, 2, 49, true)
      condition.query(schemaVal)
    }.getMessage shouldBe "fnjkdkshfns"
  }

  test("ToString") {
    val condition = new FuzzyCondition(0.5f, "name", "tr", 1, 2, 49, true)
    assertEquals("Method #toString is wrong",
      "FuzzyCondition{boost=0.5, field=name, value=tr, " +
        "maxEdits=1, prefixLength=2, maxExpansions=49, transpositions=true}",
      condition.toString)
  }
}