package de.leanovate.swaggercheck

/**
  * A request in combination with a verifier for the expected response(s).
  *
  * @param request the request
  * @param responseVerifier the verifier for the response
  * @tparam R type of the request
  * @tparam U type of the response
  */
case class OperationValidator[R, U](
                                    request: R,
                                    responseVerifier: Validator[U]
                                  )
