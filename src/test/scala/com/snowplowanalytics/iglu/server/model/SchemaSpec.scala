/*
 * Copyright (c) 2019-2022 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and
 * limitations there under.
 */
package com.snowplowanalytics.iglu.server.model

import java.time.Instant

import io.circe.literal._

import com.snowplowanalytics.iglu.core.{SchemaMap, SchemaVer}
import com.snowplowanalytics.iglu.server.model.Schema.Metadata

class SchemaSpec extends org.specs2.Specification {
  def is = s2"""
  Decode Schema $e1
  Decode SchemaBody $e2
  """

  def e1 = {
    val input = json"""
    {
      "self": {
        "vendor": "me.chuwy",
        "name": "test-schema",
        "format": "jsonschema",
        "version": "1-0-0"
      },
      "metadata": {
        "createdAt": "2019-01-12T22:12:54.777Z",
        "updatedAt": "2019-01-12T22:12:54.777Z",
        "isPublic": true
      },
      "type": "object"
    }"""

    val expected =
      Schema(
        SchemaMap("me.chuwy", "test-schema", "jsonschema", SchemaVer.Full(1, 0, 0)),
        Metadata(Instant.parse("2019-01-12T22:12:54.777Z"), Instant.parse("2019-01-12T22:12:54.777Z"), true),
        json"""{"type": "object"}"""
      )

    Schema.serverSchemaDecoder.decodeJson(input) must beRight(expected)
  }

  def e2 = {
    val selfDescribingInput = json"""
    {
      "$$schema": "http://iglucentral.com/schemas/com.snowplowanalytics.self-desc/schema/jsonschema/1-0-0#",
      "self": {
        "vendor": "me.chuwy",
        "name": "test-schema",
        "format": "jsonschema",
        "version": "1-0-0"
      },
      "type": "object"
    }"""

    val bodyOnlyInput = json"""{ "type": "object" }"""
    val invalidInput  = json"""[{ "type": "object" }]"""

    val selfDescribingResult =
      Schema.SchemaBody.schemaBodyCirceDecoder.decodeJson(selfDescribingInput) must beRight.like {
        case _: Schema.SchemaBody.SelfDescribing => ok
        case e                                   => ko(s"Unexpected decoded value $e")
      }
    val bodyOnlyResult = Schema.SchemaBody.schemaBodyCirceDecoder.decodeJson(bodyOnlyInput) must beRight.like {
      case _: Schema.SchemaBody.BodyOnly => ok
      case e                             => ko(s"Unexpected decoded value $e")
    }
    val invalidBodyResult = Schema.SchemaBody.schemaBodyCirceDecoder.decodeJson(invalidInput) must beLeft

    selfDescribingResult.and(bodyOnlyResult).and(invalidBodyResult)
  }
}
