package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.databind.annotation.{JsonSerialize, JsonDeserialize}

@JsonDeserialize(using = classOf[JsValueDeserializer])
trait JsValue {

}
