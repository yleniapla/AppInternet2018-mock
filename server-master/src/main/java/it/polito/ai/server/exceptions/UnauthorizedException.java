package it.polito.ai.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.UNAUTHORIZED, reason="Operation not permitted")
public class UnauthorizedException extends RuntimeException {}