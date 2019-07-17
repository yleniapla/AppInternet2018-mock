export class User {
	user : string;
	pass : string;
	clientid = 'client-id';
	granttype = 'password';
	authorization = 'Basic Y2xpZW50LWlkOmNsaWVudC1wYXNzd29yZA=='

	constructor(user, pass) {
		this.user = user;
		this.pass = pass;
	}
}