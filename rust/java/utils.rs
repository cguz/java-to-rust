

pub fn number_of_trailing_zeros(i_p: u32) -> i32 {
	let mut i = i_p;
	let mut num: i32 = 0;
	while num < 32 && (i & 1) == 0 {
		num += 1;
		i >>= 1;
	} 
	num
}

#[cfg(test)] 
mod tests {
	use java::utils;
	
	#[test]
	pub fn test() {
		assert!(utils::number_of_trailing_zeros(0) == 32);
		assert!(utils::number_of_trailing_zeros(0xFFFFFFFF) == 0);
		assert!(utils::number_of_trailing_zeros(0x1) == 0);
		assert!(utils::number_of_trailing_zeros(0x8) == 3);
		assert!(utils::number_of_trailing_zeros(0xFFFF0000) == 16);
		assert!(utils::number_of_trailing_zeros(0x80000000) == 31);
		assert!(utils::number_of_trailing_zeros(0xCCCCCCCC) == 2);
		assert!(utils::number_of_trailing_zeros(0xAAAAAAAA) == 1);
	}
}