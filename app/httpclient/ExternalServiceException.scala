package httpclient

class ExternalServiceException(val message: String = "") extends Exception(message) {}
