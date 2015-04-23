# LiteAU
Lite Android Utility. To be simple,clear and useful.

Example :

List<Map<String,String>> list = DBHelper.query("select name,phone from t_user");

List<User> listUser = DBHelper.query("select * from t_user",new UserCursorProcessor(cursor c){
    @Override
	public User convert(Cursor c) {
	  User u = new User();
	  u.setName(c.getString(1));
	  u.setPhone(c.getString(2));
	  u.setAge(c.getInt(3));
	  return u;
	}
});
