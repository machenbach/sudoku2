package org.mike.puzzle;

public class CantSolveException extends NoSolutionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -634939301432749835L;

	public CantSolveException() {
		// TODO Auto-generated constructor stub
	}

	public CantSolveException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public CantSolveException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public CantSolveException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public CantSolveException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
