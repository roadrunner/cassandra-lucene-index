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

import java.util
import java.util.Collections

import com.google.common.base.MoreObjects
import com.stratio.cassandra.lucene.schema.Schema
import org.apache.lucene.search.{MatchAllDocsQuery, Query}

/**
 * A [[Condition]] implementation that matches all documents.
 *
 * @author Andres de la Pena `adelapena@stratio.com`
 * @param boost The boost for this query clause. Documents matching this clause will (in addition to the normal
 */
class AllCondition(val boost : Float) extends Condition(boost) {

    /** @inheritdoc */
    override def doQuery(schema : Schema) : Query = new MatchAllDocsQuery()

    /** @inheritdoc */
    def postProcessingFields(): util.Set[String] = Collections.emptySet()

    /** @inheritdoc */
    override def toStringHelper: MoreObjects.ToStringHelper =
      toStringHelper(this)

}