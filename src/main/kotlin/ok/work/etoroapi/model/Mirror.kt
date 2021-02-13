package ok.work.etoroapi.model

data class Avatar(val width: Int, val height: Int, val type: String, val url: String)

data class Mirror(val realCID: Int, val lastName: String, val firstName: String, val aboutMe: String, val avatars: List<Avatar>)