package controllers

import configuration.CopyConfig
import model.{PageInfo, ResponsiveImageGenerator, ResponsiveImageGroup}
import model.Benefits.ComparisonItem
import model.Grid
import model.Grid._
import play.api.mvc.Controller

trait FrontPage extends Controller {

  def index =  CachedAction { implicit request =>

    val comparisonItems = Seq(
      ComparisonItem("Priority booking to all Guardian Live and Local events", false, true),
      ComparisonItem("Save 20% on Guardian Live and Local tickets", false, true),
      ComparisonItem("Bring a guest with the same discount and priority booking privileges", false, true),
      ComparisonItem("Save 20% on Guardian Masterclasses", false, true),
      ComparisonItem("Support fearless, open, independent journalism", true, true),
      ComparisonItem("Regular updates from the membership team", true, true),
      ComparisonItem("Exclusive offers and competitions", true, true),
      ComparisonItem("Membership card and annual gift", true, true),
      ComparisonItem("Highlights and live streams of selected Guardian Live events", true, true)
    )

    val pageImages = Seq(
      ResponsiveImageGroup(
        name=Some("experience"),
        altText=Some("Guardian Live event: Pussy Riot - art, sex and disobedience"),
        availableImages=ResponsiveImageGenerator(
          id="eab86e9c81414932e0d50a1cd609dccfc20ca5d2/0_0_2279_1368",
          sizes=List(500)
        )
      ),
      ResponsiveImageGroup(
        name=Some("support"),
        altText=Some("Support the Guardian"),
        availableImages=ResponsiveImageGenerator(
          id="8caacf301dd036a2bbb1b458cf68b637d3c55e48/0_0_1140_683",
          sizes=List(500)
        )
      ),
      ResponsiveImageGroup(
        name=Some("exclusive"),
        altText=Some("Exclusive content"),
        availableImages=ResponsiveImageGenerator(
          id="4bea41f93f7798ada3d572fe07b1e38dacb2a56e/0_0_2000_1200",
          sizes=List(500)
        )
      ),
      ResponsiveImageGroup(
        name=Some("brand-live"),
        altText=Some("Guardian Live"),
        availableImages=ResponsiveImageGenerator(
          id="ed27aaf7623aebc5c8c6d6c8340f247ef7b78ab0/0_0_2000_1200",
          sizes=List(500)
        )
      ),
      ResponsiveImageGroup(
        name=Some("brand-local"),
        altText=Some("Guardian Local"),
        availableImages=ResponsiveImageGenerator(
          id="889926d3c2ececf4ffd699f43713264697823251/0_0_2000_1200",
          sizes=List(500)
        )
      ),
      ResponsiveImageGroup(
        name=Some("brand-masterclasses"),
        altText=Some("Guardian Masterclasses"),
        availableImages=ResponsiveImageGenerator(
          id="ae3ad30b485e9651a772e85dd82bae610f57a034/0_0_1140_684",
          sizes=List(500)
        )
      ),
      ResponsiveImageGroup(
        name=Some("space"),
        altText=Some("A home for big ideas"),
        availableImages=ResponsiveImageGenerator(
          id="ed9347da5fc1e55721b243a958d42fca1983d012/0_0_1140_684",
          sizes=List(500)
        )
      ),
      ResponsiveImageGroup(
        name=Some("patrons"),
        altText=Some("Patrons of The Guardian"),
        availableImages=ResponsiveImageGenerator(
          id="a0b637e4dc13627ead9644f8ec9bd2cc8771f17d/0_0_2000_1200",
          sizes=List(500)
        )
      )
    )

    val midlandGoodsShedImages = Seq(
      ResponsiveImageGroup(
        metadata=Some(Grid.Metadata(
          description = Some("Midland Goods Shed and East handyside Canopy at King's Cross"),
          byline = None,
          credit = Some("John Sturrock")
        )),
        availableImages=ResponsiveImageGenerator(
          id="ae8a3ef9e568fbc5df4ceab27bf6cd0847fe3f06/0_357_8688_5213",
          sizes=List(500, 140)
        )
      ),
      ResponsiveImageGroup(
        metadata=Some(Grid.Metadata(
          description = Some("Construction work in the East handyside Canopy at King's Cross"),
          byline = None,
          credit = Some("John Sturrock")
        )),
        availableImages=ResponsiveImageGenerator(
          id="51963d023d9fa7885cad228d663104e4d04dc8b2/0_334_4998_2999",
          sizes=List(500, 140)
        )
      ),
      ResponsiveImageGroup(
        metadata=Some(Grid.Metadata(
          description = Some("Midland Goods Shed, King's Cross. Guardian Forum Event."),
          byline = None,
          credit = Some("Bennetts Associates")
        )),
        availableImages=ResponsiveImageGenerator(
          id="6adbea0e05e56945a77894aca7eb9c363789567e/27_438_4933_2961",
          sizes=List(500, 140)
        )
      ),
      ResponsiveImageGroup(
        metadata=Some(Grid.Metadata(
          description = Some("The renovation of the Midland Goods Shed Office and East Handyside Canopy, King's Cross"),
          byline = None,
          credit = Some("John Sturrock")
        )),
        availableImages=ResponsiveImageGenerator(
          id="81b36e7a40d74ff3c95c664c3b89d49914471e95/0_0_5000_2999",
          sizes=List(500, 140)
        )
      ),
      ResponsiveImageGroup(
        metadata=Some(Grid.Metadata(
          description = Some("Midland Goods Shed, King's Cross"),
          byline = None,
          credit = Some("John Sturrock")
        )),
        availableImages=ResponsiveImageGenerator(
          id="84081e14d97e33ad65d026233cdb87d4c3723d6a/206_0_4793_2875",
          sizes=List(500, 140)
        )
      ),
      ResponsiveImageGroup(
        metadata=Some(Grid.Metadata(
          description = Some("Midland Goods Shed, King's Cross"),
          byline = None,
          credit = Some("Bennetts Associates")
        )),
        availableImages=ResponsiveImageGenerator(
          id="d19c696109fd8d0be40bb8a89917555a4d7f852d/0_80_1754_1052",
          sizes=List(500, 140)
        )
      )
    )

    Ok(views.html.index(
      pageImages,
      midlandGoodsShedImages,
      comparisonItems
    ))
  }

  def welcome = CachedAction { implicit request =>
    val slideShowImages = Seq(
      ResponsiveImageGroup(
        metadata=Some(Grid.Metadata(
          description = Some("RIP Rock and Roll? (Guardian Live event): Emmy the Great"),
          byline = None, credit = None
        )),
        availableImages=ResponsiveImageGenerator(
          id="3d2be6485a6b8f5948ba39519ceb0f76007ae8d8/0_0_2280_1368",
          sizes=List(1000, 500)
        )
      ),
      ResponsiveImageGroup(
        metadata=Some(Grid.Metadata(
          description = Some("A Life in Music - George Clinton (Guardian Live event)"),
          byline = None, credit = None
        )),
        availableImages=ResponsiveImageGenerator(
          id="234dff81b39968199f501f4108189efab263a668/0_0_2280_1368",
          sizes=List(1000, 500)
        )
      ),
      ResponsiveImageGroup(
        metadata=Some(Grid.Metadata(
          description = Some("Guardian Live with Russell Brand"),
          byline = None, credit = None
        )),
        availableImages=ResponsiveImageGenerator(
          id="ecd5ccb67c093394c51f3db6779b044e3056f50c/0_0_2280_1368",
          sizes=List(1000, 500)
        )
      ),
      ResponsiveImageGroup(
        metadata=Some(Grid.Metadata(
          description = Some("A Life in Politics - Ken Clarke (Guardian Live event)"),
          byline = None, credit = None
        )),
        availableImages=ResponsiveImageGenerator(
          id="192469f1bbd69247b066a202defb23ee166ede4d/0_0_2279_1368",
          sizes=List(1000, 500)
        )
      ),
      ResponsiveImageGroup(
        metadata=Some(Grid.Metadata(
          description = Some("Guardian Live event: Pussy Riot - art, sex and disobedience"),
          byline = None, credit = None
        )),
        availableImages=ResponsiveImageGenerator(
          id="eab86e9c81414932e0d50a1cd609dccfc20ca5d2/0_0_2279_1368",
          sizes=List(1000, 500)
        )
      ),
      ResponsiveImageGroup(
        metadata=Some(Grid.Metadata(
          description = Some("A Life in Music - George Clinton (Guardian Live event)"),
          byline = None, credit = None
        )),
        availableImages=ResponsiveImageGenerator(
          id="eccf14ef0f9f4b672b3a7cc594676aa498827f4a/0_0_2280_1368",
          sizes=List(1000, 500)
        )
      ),
      ResponsiveImageGroup(
        metadata=Some(Grid.Metadata(
          description = Some("Behind the Headlines - What's all the fuss about feminism? (Guardian Live event): Bonnie Greer"),
          byline = None, credit = None
        )),
        availableImages=ResponsiveImageGenerator(
          id="99c490b1a0863b3d30718e9985693a3ddcc4dc75/0_0_2280_1368",
          sizes=List(1000, 500)
        )
      )
    )

    Ok(views.html.welcome(slideShowImages))
  }
}

object FrontPage extends FrontPage
