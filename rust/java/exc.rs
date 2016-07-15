use std::rc::*;
use std::fmt;

pub trait Exception: fmt::Debug {
	fn get_message(&self) -> Rc<String>;
	fn get_cause(&self) -> Option<Rc<Exception>>;
	fn get_type(&self) -> &'static str;
}

pub struct Void {
	
}

impl Void {
	pub fn new() -> Void {
		Void {
			
		}
	}
}


#[derive(Clone)]
pub struct Exc {
	cause: Option<Rc<Exception>>,
	message: Rc<String>,
	exception_type: &'static str 
}

impl Exception for Exc {
	fn get_message(&self) -> Rc<String> {
		self.message.clone()
	}
	fn get_cause(&self) -> Option<Rc<Exception>> {
		self.cause.clone()
	}
	fn get_type(&self) -> &'static str {
		return self.exception_type;
	}	
}

impl fmt::Debug for Exc {
	 fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "Exception {{ message: {}, type: {}}}", self.message, self.exception_type)
    }	
}

impl Exc {
	pub fn new(exception_type: &'static str) -> Rc<Exception> {
		Rc::new(Exc {
			exception_type: exception_type,
			cause: None,
			message: Rc::new("".to_string())
		})
	}
	pub fn new_msg(exception_type: &'static str, message: String) -> Rc<Exception> {
		Rc::new(Exc {
			exception_type: exception_type,
			cause: None,
			message: Rc::new(message)
		})
	}
	
	pub fn new_cause(exception_type: &'static str, cause: Rc<Exception>) -> Rc<Exception> {
		Rc::new(Exc {
			exception_type: exception_type,
			cause: Some(cause),
			message: Rc::new("".to_string())
		})
	}
	
	pub fn new_msg_cause(exception_type: &'static str, message: String, cause: Rc<Exception>) -> Rc<Exception> {
		Rc::new(Exc {
			exception_type: exception_type,
			cause: Some(cause),
			message: Rc::new(message)
		})
	}
}


#[cfg(test)] 
mod tests {
	use java::exc::Exception;
	use java::exc::Exc;
	use std::rc::*;
	
	fn test_res() -> Result<i32, Rc<Exception>> {
		let exc = Exc::new_msg("test", format!("{}: {}", "innerstring", 1));
		let exc2 = Exc::new_msg_cause("test", format!("{}: {}", "innerstring2", 2), exc);
		return Err(exc2.clone());	
	}
	
	#[test]
	pub fn test1() {
		match test_res() {
			Err(exc) => println!("{} {}",exc.get_type(), &exc.get_message()),
			_ => println!("no exc")
		}
		
	}
	
}

