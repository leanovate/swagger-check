package de.leanovate.swaggercheck

package object simple {
  implicit val requestCreator: RequestCreator[SimpleRequest] = SimpleRequest.creator

  implicit val responseExtrator: ResponseExtractor[SimpleResponse] = SimpleResponse.extractor

  type SimpleOperationVerifier = OperationVerifier[SimpleRequest, SimpleResponse]
}
