update [dbo].[ofProperty] set propValue = 'com.pcitc.openfire.plugin.WebServiceAuthProvider' where name = 'provider.auth.className'

--服务ContentType
insert into [dbo].[ofProperty]([name] ,[propValue])
values ('webServiceAuthProvider.authMethodContentType', @authMethodContentType)

--服务SOAPRequest字符串 例如
/**<?xml version="1.0" encoding="utf-8"?>
<soap12:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
  <soap12:Body>
    <GetUserState xmlns="http://tempuri.org/">
      <loginName>string</loginName>
      <passWord>string</passWord>
    </GetUserState>
  </soap12:Body>
</soap12:Envelope>
**/
insert into [dbo].[ofProperty]([name] ,[propValue])
values ('webServiceAuthProvider.authMethodSOAPRequest', @authMethodSOAPRequest)

--服务返回用户email节点名
insert into [dbo].[ofProperty]([name] ,[propValue])
values ('webServiceAuthProvider.emailField', @emailField)

--服务返回认证成功标识节点名
insert into [dbo].[ofProperty]([name] ,[propValue])
values ('webServiceAuthProvider.flagField', @flagField)

--服务地址
insert into [dbo].[ofProperty]([name] ,[propValue])
values ('webServiceAuthProvider.http', @http)

--服务返回用户姓名节点名
insert into [dbo].[ofProperty]([name] ,[propValue])
values ('webServiceAuthProvider.nameField', @nameField)

--服务请求的密码节点名
insert into [dbo].[ofProperty]([name] ,[propValue])
values ('webServiceAuthProvider.passwordField', @passwordField)

--服务请求的密码节点名
insert into [dbo].[ofProperty]([name] ,[propValue])
values ('webServiceAuthProvider.usernameField', @usernameField)



update [dbo].[ofProperty] set propValue = 'com.pcitc.openfire.plugin.WebServiceUserProvider' where name = 'provider.user.className'

--获取所有用户方法的SOAPRequest，例如：
/*<?xml version="1.0" encoding="utf-8"?>
<soap12:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
  <soap12:Body>
    <GetAllUser xmlns="http://tempuri.org/" />
  </soap12:Body>
</soap12:Envelope>
*/
insert into [dbo].[ofProperty]([name] ,[propValue])
values ('webServiceUserProvider.allUsersMethodSOAPRequest', @allUsersMethodSOAPRequest)

--获取所有用户方法的ContentType
insert into [dbo].[ofProperty]([name] ,[propValue])
values ('webServiceUserProvider.allUsersMethodContentType', @allUsersMethodContentType)

