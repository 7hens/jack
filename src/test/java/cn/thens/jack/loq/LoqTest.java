package cn.thens.jack.loq;

import org.junit.Before;
import org.junit.Test;

public class LoqTest {
    @Before
    public void init() {
        LoqX.setDefaultInstance(Loq.logger(new PrettyLogger(Logger.SYSTEM)));
    }

    @Test
    public void longText() {
        Loq.d(LoqX.time("longText"));
        Loq.e(LONG_TEXT + LONG_TEXT + LONG_TEXT + LONG_TEXT + "\n" + LONG_TEXT);
        Loq.d(LoqX.time("longText"));
    }

    @Test
    public void error() {
        Loq.e(new Throwable());
    }

    private static final String LONG_TEXT = "人之所以很难理解“想象的秩序”这种概念，是因为人觉得现实只有两类：客观现实和主观现实。所谓“客观现实”，就是事物的存在与我们的信念和感受无关。例如重力就是一个客观现实，早在牛顿之前便已存在，而且不论我们信与不信，都会受到重力影响。" +
            "相反，主观现实取决于个人的信念和感受。例如，假设我觉得头一阵剧痛，于是去看医生。医生对我的头部做了彻底检查，却没查出什么问题。于是她又要我去做血液检查、尿液检查、DNA检测、拍X光片、做心电图、做功能性磁共振成像等等。等到结果一出，她说我完全健康，可以回家了。可是我仍然觉得头痛得不得了。所有客观测试都找不出我有什么问题，除了我以外没人感觉痛苦，但对我来说，这种痛苦百分之百真实。" +
            "多数人以为，现实只有客观或主观两种，没有第三种可能。于是，只要他们说服自己某件事并非出于自己的主观感受，就贸然认为这件事必然属于客观。如果有那么多人相信上帝，如果钱确实能让世界运转，如果民族主义会发动战争，也会建立帝国，那么这一切一定不只是我个人的主观信念。也就是说，上帝、金钱和国家一定是客观的现实啰？" +
            "然而，现实还有第三个层次：互为主体（intersubjective）。这种互为主体的现实，并不是因为个人的信念或感受而存在，而是依靠许多人类的沟通互动而存在。历史上有许多最重要的驱动因素，都具有互为主体的概念。比如金钱并没有客观价值，1美元不能吃、不能喝，也不能拿来穿。但只要有几十亿人都相信它的价值，你就可以拿它来买吃的、买喝的、买穿的。如果有位面包师忽然不再相信美元了，不愿意让我用这张绿色的纸换他的面包，也没什么关系，只要再走几条街，就有另一家超市可买。然而，如果超市的收银员、市场的小贩、购物商场的销售员一律拒绝接受这张纸，美元就会失去价值。当然，这些绿色的纸张还是存在，但它们已经再无用处。" +
            "这种事情其实时不时就会发生。1985年11月3日，缅甸政府毫无预警地宣布25缅元、50缅元和100缅元的纸钞不再是法定货币。民众根本没有兑换纸钞的机会，一辈子的积蓄瞬间成了几堆毫无价值的废纸。为了取代失效的货币，政府发行了新的75缅元纸钞，声称要纪念缅甸奈温将军（General Ne Win）的75岁生日。1986年8月，政府又发行了15缅元和35缅元的纸钞。据传，奈温迷信数字，相信15和35是幸运数字。但对国民来说，可就一点也不幸运了。到了1987年9月5日，政府又突然下令，所有35缅元和75缅元的纸钞同样不再是法定货币。" +
            "像这样因为人类不再相信而一夕蒸发的，不是只有金钱的价值。同样的事情也可能发生在法律、神，甚至整个帝国上。这一秒它们还在忙着塑造世界，下一秒却已不复存在。天神宙斯和天后赫拉曾经是地中海一带的重要力量，但现在不再有人相信，也就令它们失去了力量。苏联曾经一度能够毁灭全人类，但也是在一支笔的力量下便烟消云散。1991年12月8日，在维斯库里（Viskuli）附近的一幢乡间大宅，俄罗斯、乌克兰和白俄罗斯的领导人签署了《别洛韦日协定》其中声明：“吾等白俄罗斯共和国、俄罗斯联邦暨乌克兰，作为1922年苏联成立条约之签署创始国，兹声明终止苏联作为国际法主体及地缘政治现实。” 就这样，苏联从此解体。" +
            "要说金钱是个互为主体的现实，相对还比较容易接受。大多数人也愿意承认，那些古希腊神明、邪恶的帝国和异国文化价值观都只是一种想象。但如果说的是自己的神、自己的国家、自己的价值观，因为正是这些给了我们生命的意义，要再说这些都是虚构的，就没那么容易接受了。我们希望相信自己的生命有客观意义，希望自己的种种牺牲不只是为了脑子里的各种空想。但事实上，大多数人生活的意义，都只存在于彼此讲述的故事之中。" +
            "在大家一起编织出共同故事网的那一刻，意义就产生了。对我来说，在教堂结婚、在斋戒月禁食或在选举日投票这些行为为什么有意义？原因就在于我的父母也认为这有意义，还有我的兄弟姐妹、邻居朋友、附近城市的居民，甚至是遥远异国的民众，都认为这有意义。为什么这些人都认为这有意义？因为他们的朋友邻居也有同样的看法。人类会以一种不断自我循环的方式，持续增强彼此的信念。每一次互相确认，都会让这张意义的网收得更紧，直到你别无选择，只能相信大家都相信的事。" +
            "不过，经过几十年、几世纪，意义的网也可能忽然解体，而由一张新的网取而代之。读历史就是在看这些网的编织和解体，并让人意识到，对这个世代的人来说最重要的事情，很有可能对他们的后代就变得毫无意义。" +
            "1187年，萨拉丁（Saladin）在哈丁战役（Battle of Hattin）中击败十字军，占领了耶路撒冷。教皇因此发起了第三次十字军东征，希望夺回圣城。让我们假设有位名叫约翰的年轻英国贵族，远离家乡征讨萨拉丁。约翰相信，自己这么做是有客观意义的，如果自己在东征过程中牺牲，灵魂就能升上天堂，享受永恒的无上喜悦。如果这时候跟他说，灵魂和天堂都是人类编出来的故事，肯定会把他吓坏。约翰一心相信，如果他抵达圣地，却被一个长着大胡子的穆斯林战士一斧头劈在头上，他当然会痛苦万分、两耳嗡嗡、两腿一软、眼前一黑——然后就会突然发现自己被一片明亮的光芒笼罩，听到天使的歌声、悠扬的竖琴，看到发着光、有着翅膀的天使召唤他通过一道雄伟的金色大门。" +
            "约翰对这一切的信念之所以这么强烈，是因为有一张细细密密而且极其强大的意义之网包覆着他。他最早的记忆，就是亨利爷爷有一把生锈的剑，挂在古堡的主厅。当他还在蹒跚学步时，就听过亨利爷爷在第二次十字军东征中战死的故事，说爷爷现在已经在天堂安息，有天使做伴，一直护佑着约翰和他的家人。吟游诗人来访城堡时，常常吟唱着十字军在圣地英勇作战的歌谣。约翰上教堂的时候，喜欢看彩绘玻璃窗，其中一扇正是布永的戈弗雷（Godfrey of Bouillon，第一次十字军东征的领导者）拿长枪刺穿一个面容邪恶的敌人，另一扇则是罪人的灵魂在地狱里燃烧。约翰也会认真听当地神父的讲道，那是他认识的最有学问的人。几乎每个礼拜天，神父都会搭配各种精心设计的比喻和令人莞尔的笑话，讲述着世上只有天主教是唯一的救赎，罗马教皇是我们神圣的父，我们必须听从他的指示。如果我们杀人或偷窃，上帝会让我们下地狱；但如果我们杀的是异教徒，上帝会欢迎我们上天堂。" +
            "在约翰刚满18岁的一天，一位骑士骑马狼狈地来到城堡大门，语带哽咽地宣布：十字军在哈丁被萨拉丁击败了！耶路撒冷沦陷！教皇宣布将发动新一波十字军东征，并承诺不幸丧生者将得到永恒的救赎！身边所有人看来既震惊又忧虑，但约翰脸上发出超脱俗世的光亮，宣告：“我将对战异教徒，收复圣地！”众人静了一下，接着脸上露出笑容，流下感动的泪水。母亲擦擦眼泪，紧紧抱着约翰，说她有多么引以为荣。他的父亲则在他背上大力拍了一掌，说道：“儿子，如果我还是你这年纪，必会和你同行。事关我们家族的荣誉，相信你一定不会让我们失望！”他有两个朋友也宣布要一同从军。而且，就连约翰的死对头、那个住在河对岸的男爵，也特地来家里拜访，祝他一路顺利。" +
            "当他离开城堡时，村民纷纷从小屋里出来，向他挥手致意，而对于这个即将前去对抗异教徒的十字军勇士，所有的美丽姑娘都露出崇拜的眼神。他从英国出航，驶过各个陌生而遥远的地方，例如诺曼底、普罗旺斯、西西里岛，许多异国的骑士纷纷加入，大家都有着共同的目标、共同的信念。但等到军队终于在圣地上岸，开始与萨拉丁的部下战斗，约翰才惊讶地发现，这些邪恶的撒拉逊人怎么和自己有同样的信念。当然，想必撒拉逊人也没搞清楚，竟然以为基督徒才是异教徒，而穆斯林则是服从神的旨意。但撒拉逊人也接受同样的基本原则，也就是为神和耶路撒冷而战的战士如果战死，将会直接上天堂。" +
            "就这样，中世纪文化一丝一缕地编织着意义的网，把约翰和同时代的人都像苍蝇一样捕进网中。约翰绝不可能想象得到，这一切故事都只是出于想象虚构。说他的父母和叔伯都错了还有可能，但还有吟游诗人、他所有的朋友、村里的姑娘、知识渊博的神父、住在河对岸的男爵、在罗马的教皇、普罗旺斯和西西里岛的骑士，甚至还包括那些穆斯林，难道真有可能这些人都在胡思乱想？" +
            "时间就这么过了好多年。在历史学家的注视下，意义的网被拆散，又张起了一张新的网。约翰的父母已经故去，他的兄弟姐妹也不在人世。这时已经不再有吟游诗人唱着十字军东征的故事，新流行的是剧院上演的爱情悲剧。家族的城堡被烧成一片平地，重建之后，亨利爷爷的剑已经难觅影踪。教堂的彩绘玻璃在一次冬季的狂风中破碎，换上的玻璃不再描绘布永的戈弗雷和地狱里的罪人，而是英国国王打败法国国王的伟大胜利。当地的牧师已经不再称呼教皇为“我们神圣的父”，而是“罗马的那个魔鬼”。在附近的大学里，学者钻研着古希腊手稿、解剖尸体，并在紧闭的门后窃窃私语，说着或许根本没有灵魂这种东西。" +
            "时间转眼又过了好多年。原本是城堡的地方，现在成了购物商场。在当地的电影院里，《巨蟒与圣杯》（Monty Python and the Holy Grail ）已经放映了无数次。而在一座空教堂里，无聊的牧师看到两名日本游客简直喜出望外，开始滔滔不绝地解说教堂里的彩绘玻璃，游客礼貌地频频点头微笑，但完全没听懂。在外面的阶梯上，一群青少年正用iPhone手机在YouTube上看约翰·列侬那首《想象》（Imagine）的混录版。约翰·列侬唱着：“想象这个世界没有天堂，只要你想象，这事很轻松。”一名巴基斯坦清洁工正在打扫人行道，旁边有台收音机播报着新闻：叙利亚屠杀仍在继续，安理会会议落幕但未能达成任何协议。突然之间一条时光隧道打开，一道神秘的光照在其中一位青少年的脸上，他宣告：“我将对战异教徒，收复圣地！”" +
            "异教徒？圣地？对于现在绝大多数英格兰人来说，这些词语已经不再有任何意义。就连那位牧师，也可能觉得这个年轻人是精神病发作。相反，如果一位英国青年决定加入国际特赦组织，前往叙利亚保护难民人权，现在大家会觉得他是个英雄，但在中世纪，大家会觉得这人疯了。在12世纪的英格兰，没有人知道什么叫人权。你要大老远跑到中东，冒着生命危险，而且居然不是去杀穆斯林，而是保护一群穆斯林别被另一群穆斯林杀了？你的脑子绝对出了很大的问题。" +
            "这正是历史展开的方式。人类编织出一张意义的网，并全然相信它，但这张网迟早都会拆散，直到我们回头一看，实在无法想象当时怎么可能有人真心相信这样的事。事后看来，为了进入天堂而参加十字军，听起来就像彻底疯了。事后看来，冷战似乎是件更疯狂的事。不过才短短30年前，怎么可能有人因为相信能打造出人间天堂，就不惜为此冒着核弹浩劫的危险？而在现在的100年后，我们现在对民主和人权的信念，也有可能会让我们的后代感到同样难以理解。";
}
